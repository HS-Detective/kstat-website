(function () {
  // ---------- helpers ----------
  const $ = (s) => document.querySelector(s);
  const fmt = (n) => Number(n || 0).toLocaleString('en-US');
  const fmtUSD = (n) => `USD ${fmt(n)}`;
  const qs = (o) => new URLSearchParams(o).toString();

  async function getJSON(url){
    const res = await fetch(url,{credentials:'same-origin'});
    if(!res.ok) throw new Error('HTTP '+res.status);
    const ct = res.headers.get('content-type') || '';
    if(!ct.includes('application/json')){
      const t = await res.text(); throw new Error('JSON 아님: ' + t.slice(0,200));
    }
    return res.json();
  }

  // y축을 '백만' 단위로 보이게 (표기만 스케일, 데이터는 그대로)
  const tickMillions = (v) => fmt(Math.round(v/1_000_000));

  function tooltipUSD(){
    return { callbacks:{
      label:(c)=>{
        const v = c.parsed?.y ?? c.raw;
        const key = c.dataset?.label ? c.dataset.label + ': ' : '';
        return key + fmtUSD(v);
      }
    }};
  }

  // Chart 재사용 (완전 교체)
  function ensureChart(canvas, type, data, options = {}){
    if(canvas.__chart){
      const ch = canvas.__chart;
      ch.config.type = type; ch.data = data; ch.options = options; ch.update();
      return ch;
    }
    const inst = new Chart(canvas, {type, data, options});
    canvas.__chart = inst;
    return inst;
  }

  // ---------- 한국 기준 방향 표기 도우미 ----------
  const countryLabel = (c) => (!c || c === 'ALL') ? '전체' : c;
  const dirLabel = (metric, c) =>
    metric === 'export' ? `한국 → ${countryLabel(c)}` : `한국 ← ${countryLabel(c)}`;

  // ---------- DOM refs ----------
  const metricEl = $('#ti-metric'); // 전역 지표
  const t1 = { from:$('#ti1-from'), to:$('#ti1-to'), country:$('#ti1-country'), mti4:$('#ti1-mti4') };
  const t2 = { year:$('#ti2-year'), mti4:$('#ti2-mti4') };
  const t3 = { year:$('#ti3-year'), country:$('#ti3-country') };

  const MIN_YM = '2022-01', MAX_YM = '2024-12';
  const clampYm = (ym)=> (/^\d{4}-\d{2}$/.test(ym||'')) ? (ym<MIN_YM?MIN_YM:(ym>MAX_YM?MAX_YM:ym)) : null;
  const ensureOrder = (a,b)=> a<=b ? [a,b] : [b,a];

  // ---------- 옵션 로드 ----------
  async function loadOptions(){
    try{
      const [countries, mti4] = await Promise.all([
        getJSON('/api/stats/options/countries?limit=20'),
        getJSON('/api/stats/options/mti4?limit=20')
      ]);

      [t1.country, t3.country].forEach(sel=>{
        if(!sel) return;
        sel.innerHTML = `<option value="ALL">전체(ALL)</option>` +
          countries.map(v=>`<option value="${v}">${v}</option>`).join('');
        sel.value = countries.includes('미국') ? '미국' : (countries[0] || 'ALL');
      });

      [t1.mti4, t2.mti4].forEach(sel=>{
        if(!sel) return;
        sel.innerHTML = `<option value="ALL">ALL (전체)</option>` +
          mti4.map(x=>{
            const displayText = x.label ? `${x.label} (${x.code})` : x.code;
            return `<option value="${x.code}">${displayText}</option>`;
          }).join('');
        sel.value = 'ALL';
      });
    }catch(e){ console.warn('[옵션 로드 실패]', e); }
  }

  // ---------- charts ----------
  async function renderTrend(){
    const wrap = $('#ti-card1'); const empty = wrap.querySelector('.ti-empty');
    const canvas = $('#ti-chart-trend');

    let startYm = clampYm(t1.from?.value || '2023-01') || '2023-01';
    let endYm   = clampYm(t1.to  ?.value || '2024-12') || '2024-12';
    [startYm,endYm] = ensureOrder(startYm,endYm);

    // 선택 국가(방향 표기에 사용)
    const country = (t1.country?.value || 'ALL').trim() || 'ALL';

    try{
      const rows = await getJSON('/api/stats/monthly-trend?' + qs({
        metric: metricEl.value,
        country,
        mti4: (t1.mti4?.value || 'ALL').trim().toUpperCase(),
        startYm, endYm
      }));

      empty && (empty.hidden = rows.length > 0);

      // 기간 칩
      $('#ti-chip-range') && ($('#ti-chip-range').textContent =
        `표시기간 ${startYm.replace('-','.') } ~ ${endYm.replace('-','.') }`);

      // 지표 칩 (방향 포함)
      const isExport = metricEl.value === 'export';
      const chip = $('#ti-chip-metric');
      if(chip){
        chip.textContent = `${isExport ? '수출' : '수입'} (${dirLabel(metricEl.value, country)})`;
        chip.classList.toggle('is-import', !isExport);
      }

      const lineColor = isExport ? '#2563eb' : '#ef4444';
      const fillColor = isExport ? 'rgba(37,99,235,.12)' : 'rgba(239,68,68,.12)';

      ensureChart(canvas,'line',{
        labels: rows.map(r=>r.label),
        datasets:[{
          label: isExport ? '수출' : '수입',
          data: rows.map(r=>r.value),
          borderColor: lineColor, backgroundColor: fillColor, fill:true,
          tension:.25, pointRadius:2, pointBackgroundColor: lineColor, pointBorderColor: lineColor
        }]
      },{
        responsive:true, maintainAspectRatio:false,
        plugins:{
          tooltip: tooltipUSD(),
          legend:{display:false},
          // 차트 타이틀에 방향+단위 표시
          title:{
            display:true,
            text: `${dirLabel(metricEl.value, country)} ${isExport ? '수출' : '수입'}`,
            color:'#334155', font:{weight:'700', size:13}, padding:{bottom:8}
          }
        },
        scales:{
          y:{ ticks:{ callback: tickMillions }, grid:{ color:'#eef2f7' } },
          x:{ grid:{ color:'#f7f8fb' } }
        }
      });

    }catch(e){ console.error('[monthly-trend]', e); empty && (empty.hidden=false); }
  }

  async function renderTop(){
    const wrap = $('#ti-card2'); const empty = wrap.querySelector('.ti-empty');
    const canvas = $('#ti-chart-top');

    try{
      const rows = await getJSON('/api/stats/top-countries?' + qs({
        metric: metricEl.value, year: t2.year?.value,
        mti4: (t2.mti4?.value || 'ALL').trim().toUpperCase(), topN:5
      }));

      empty && (empty.hidden = rows.length > 0);

      // 칩 텍스트 (한국 기준 표기)
      const isExport = metricEl.value === 'export';
      const yChip = $('#ti2-chip-year');
      const mChip = $('#ti2-chip-metric');
      if (yChip) yChip.textContent = t2.year?.value || '';
      if (mChip) {
        mChip.textContent = `${isExport ? '수출' : '수입'} (한국 기준)`;
        mChip.classList.toggle('is-import', !isExport);
      }

      ensureChart(canvas,'bar',{
        labels: rows.map(r=>r.label),
        datasets:[{
          label:'금액', data: rows.map(r=>r.value),
          backgroundColor:'#93c5fd', borderColor:'#60a5fa', borderWidth:1
        }]
      },{
        responsive:true, maintainAspectRatio:false,
        plugins:{
          tooltip: tooltipUSD(),
          legend:{display:false},
          // 타이틀
          title:{
            display:true,
            text:`${isExport ? '수출' : '수입'} (한국 기준)`,
            color:'#334155', font:{weight:'700', size:13}, padding:{bottom:8}
          }
        },
        scales:{
          y:{ ticks:{ callback: tickMillions }, grid:{ color:'#eef2f7' } },
          x:{ ticks:{autoSkip:false, maxRotation:0}, grid:{ display:false } }
        },
        datasets:{ bar:{ borderRadius:8 } }
      });

    }catch(e){ console.error('[top-countries]', e); empty && (empty.hidden=false); }
  }

  async function renderShare(){
    const wrap = $('#ti-card3'); const empty = wrap.querySelector('.ti-empty');
    const canvas = $('#ti-chart-share');

    // 선택 국가(방향 표기에 사용)
    const country = (t3.country?.value || '미국').trim() || '미국';

    try{
      const rows = await getJSON('/api/stats/item-share?' + qs({
        metric: metricEl.value, year: t3.year?.value,
        country, topN:10
      }));

      empty && (empty.hidden = rows.length > 0);

      // 칩 텍스트
      const isExport = metricEl.value === 'export';
      const cChip = $('#ti3-chip-country');
      const yChip = $('#ti3-chip-year');
      const mChip = $('#ti3-chip-metric');
      if (cChip) cChip.textContent = country;
      if (yChip) yChip.textContent = t3.year?.value || '';
      if (mChip){
        mChip.textContent = `${isExport ? '수출' : '수입'} (${dirLabel(metricEl.value, country)})`;
        mChip.classList.toggle('is-import', !isExport);
      }

      ensureChart(canvas,'doughnut',{
        labels: rows.map(r=>r.label),
        datasets:[{
          data: rows.map(r=>r.value),
          backgroundColor:['#60a5fa','#f472b6','#f59e0b','#34d399','#22d3ee','#c084fc','#fb7185','#4ade80','#a3e635','#f97316'],
          borderWidth:0
        }]
      },{
        responsive:true, maintainAspectRatio:false,
        cutout:'64%',
        plugins:{
          tooltip: tooltipUSD(),
          legend:{ position: (window.innerWidth>=1100?'right':'bottom'), labels:{ boxWidth:12, usePointStyle:true } },
          // 타이틀
          title:{
            display:true,
            text:`${dirLabel(metricEl.value, country)} ${isExport ? '수출' : '수입'}`,
            color:'#334155', font:{weight:'700', size:13}, padding:{bottom:8}
          }
        }
      });

    }catch(e){ console.error('[item-share]', e); empty && (empty.hidden=false); }
  }

  // 도넛 중앙 합계
  const centerText = {
    id:'centerText',
    afterDraw(chart){
      if(chart.config.type!=='doughnut') return;
      const meta = chart.getDatasetMeta(0); if(!meta?.data?.[0]) return;
      const {x,y} = meta.data[0]; const ds = chart.data.datasets?.[0]; if(!ds?.data?.length) return;
      const sum = ds.data.reduce((a,b)=>a+(+b||0),0);
      const ctx = chart.ctx; ctx.save(); ctx.textAlign='center'; ctx.fillStyle='#334155';
      ctx.font='700 13px "Noto Sans"'; ctx.fillText('Top10 합계', x, y-8);
      ctx.font='800 16px "Noto Sans"'; ctx.fillText(`USD ${Number(sum).toLocaleString('en-US')}`, x, y+12);
      ctx.restore();
    }
  };
  Chart.register(centerText);

  function refreshAll(){ renderTrend(); renderTop(); renderShare(); }
  window.refreshAll = refreshAll;

  // 이벤트
  metricEl?.addEventListener('change', refreshAll);
  [t1.from,t1.to,t1.country,t1.mti4].forEach(el=> el?.addEventListener('change', renderTrend));
  [t2.year,t2.mti4].forEach(el=> el?.addEventListener('change', renderTop));
  [t3.year,t3.country].forEach(el=> el?.addEventListener('change', renderShare));

  // 모든 툴팁 토글(아래로 내려가던 문제 포함: 전역 닫힘 처리)
  document.addEventListener('click', (e)=>{
    const btn = e.target.closest('.ti-info-btn');
    document.querySelectorAll('.ti-tooltip').forEach(t => t.classList.remove('is-open'));
    if(btn){ btn.closest('.ti-tooltip')?.classList.add('is-open'); }
  });

  // 부트
  document.addEventListener('DOMContentLoaded', async ()=>{
    await loadOptions();
    refreshAll();
  });
})();