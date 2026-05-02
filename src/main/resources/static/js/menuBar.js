document.addEventListener('DOMContentLoaded', () => {
    
    const submenuData = {
        '국내통계': {
            title: '국내통계',
            desc: '한국의 수출입 통계, 대북 무역통계와<br>간접수출 통계를 확인할 수 있습니다.<br>품목별, 국가별 통계 및 일부 항목은<br>로그인 후 이용 가능합니다.',
            columns: [
                {
                    title: '한국무역',
                    items: ['<a href= "https://stat.kita.net/stat/kts/sum/SumImpExpTotalList.screen">수출입 총괄</a>',
                      '<a href="https://stat.kita.net/stat/kts/use/BecList.screen">가공단계 수출입</a>',
                     '<a href="https://stat.kita.net/stat/kts/pum/ItemImpExpList.screen">품목 수출입</a>',
                     '<a href="https://stat.kita.net/stat/kts/ctr/CtrTotalImpExpList.screen">국가 수출입</a>',
                     '<a href="https://stat.kita.net/stat/kts/rel/RelColligationList.screen">대륙/경제권 수출입</a>',
                     '<a href="https://stat.kita.net/stat/kts/prod/ProdWholeList.screen">지자체 수출입</a>',
                     '<a href="https://stat.kita.net/stat/kts/port/PortImpExpList.screen">항구/공항 수출입</a>',
                     '<a href="https://stat.kita.net/stat/kts/fta/FtaCtrImpExpList.screen">FTA 체결국 수출입</a>']
                },
                {
                    title: '<a href="https://stat.kita.net/stat/istat/kpts/KptsWholeList.screen">북한무역</a>',
                    items: []
                },
                {
                    title: '간접수출',
                    items: ['<a href="https://stat.kita.net/stat/ind/KtsIndMain.screen">간접수출 현황</a>',
                      '<a href="https://stat.kita.net/stat/ind/kind/SumKtsIndTotalList.screen">간접수출 총괄</a>']
                }
            ]
        },
        '해외통계': {
            title: '해외통계',
            desc: '세계 각국의 무역통계를 확인할 수 있습니다.<br>국가별 통계 업데이트 일자를 확인하세요.<br>각 메뉴 내 국가별, 품목별 통계 항목은<br>로그인 후 이용 가능합니다.',
            columns: [
                {
                    title: '아시아',
                    items: ['<a href="https://stat.kita.net/stat/istat/asean/AseanWholeList.screen">ASEAN (총 10개국)</a>',
                      '<a href="https://stat.kita.net/stat/istat/twts/TwtsWholeList.screen">대만 (2025.04)</a>',
                     '<a href="https://stat.kita.net/stat/istat/its/ItsWholeList.screen">인도 (2024.12)</a>',
                     '<a href="https://stat.kita.net/stat/istat/uzts/UztsWholeList.screen">우즈베키스탄 (2024)</a>',
                     '<a href="https://stat.kita.net/stat/istat/jts/JtsWholeList.screen">일본 (2025.05)</a>',
                     '<a href="https://stat.kita.net/stat/istat/cts/CtsWholeList.screen">중국 (2025.06)</a>',
                     '<a href="https://stat.kita.net/stat/istat/hkts/HktsWholeList.screen">홍콩 (2023)</a>',
                     '<a href="https://stat.kita.net/stat/istat/kzts/KztsWholeList.screen">카자흐스탄 (2024.12)</a>',
                     '<a href="https://stat.kita.net/stat/istat/trts/TrtsWholeList.screen">튀르키예 (2025.05)</a>']
                },
                {
                    title: '유럽',
                    items: ['<a href="https://stat.kita.net/stat/istat/euts/EuWholeList.screen">EU (총 27개국)</a>',
                     '<a href="https://stat.kita.net/stat/istat/chts/ChtsWholeList.screen">스위스 (2025.06)</a>',
                     '<a href="https://stat.kita.net/stat/istat/nots/NotsWholeList.screen">노르웨이 (2025.05)</a>',
                     '<a href="https://stat.kita.net/stat/istat/ruts/RutsWholeList.screen">러시아 (2022.01)</a>',
                     '<a href="https://stat.kita.net/stat/istat/ukts/UktsWholeList.screen">영국 (2022.11)</a>',
                     '<a href="https://stat.kita.net/stat/istat/uats/UatsWholeList.screen">우크라이나 (2024)</a>']
                },
                {
                    title: '북미',
                    items: ['<a href="https://stat.kita.net/stat/istat/uts/UsWholeList.screen">미국 (2025.05)</a>',
                     '<a href="https://stat.kita.net/stat/istat/cats/CatsWholeList.screen">캐나다 (2025.05)</a>']
                },
                {
                    title: '중남미',
                    items: ['<a href="https://stat.kita.net/stat/istat/mxts/MxtsWholeList.screen">멕시코 (2023.12)</a>',
                     '<a href="https://stat.kita.net/stat/istat/brts/BrtsWholeList.screen">브라질 (2025.05)</a>',
                     '<a href="https://stat.kita.net/stat/istat/clts/CltsWholeList.screen">칠레 (2025.05)</a>',
                     '<a href="https://stat.kita.net/stat/istat/cots/CotsWholeList.screen">콜롬비아 (2025.03)</a>',
                     '<a href="https://stat.kita.net/stat/istat/pets/PetsWholeList.screen">페루 (2025.03)</a>']
                },
            {
                    title: '오세아니아',
                    items: ['<a href="https://stat.kita.net/stat/istat/nzts/NztsWholeList.screen">뉴질랜드 (2025.06)</a>',
                     '<a href="https://stat.kita.net/stat/istat/auts/AutsWholeList.screen">호주 (2025.05)</a>']
                },
                {
                    title: '중동',
                    items: ['<a href="https://stat.kita.net/stat/istat/sats/SatsWholeList.screen">사우디아라비아 (2024)</a>']
                },
                {
                    title: '아프리카',
                    items: ['<a href="https://stat.kita.net/stat/istat/zats/ZatsWholeList.screen">남아공 (2025.05)</a>']
                }
            ]
        },
        'IMF 세계통계': {
            title: 'IMF 세계통계',
            desc: 'IMF가 제공한 세계무역 통계와<br>주요국이 공개한 무역 데이터를<br>도표 및 그래프로 확인할 수 있습니다.',
            columns: [
                {
                    title: '<a href="https://stat.kita.net/stat/world/trade/CtrImpExpList.screen">세계무역 (2025.03)</a>',
                    items: []
                },
                {
                    title: '무역통계로 보는 주요국',
                    items: ['<a href="https://stat.kita.net/stat/world/major/KoreaStats.screen">한국</a>',
                     '<a href="https://stat.kita.net/stat/world/major/USStats.screen">미국</a>',
                     '<a href="https://stat.kita.net/stat/world/major/ChinaStats.screen">중국</a>',
                     '<a href="https://stat.kita.net/stat/world/major/EUStats.screen">EU</a>',
                     '<a href="https://stat.kita.net/stat/world/major/JapanStats.screen">일본</a>']
                }
            ]
        },
        '맞춤분석': {
            title: '맞춤분석',
            desc: '원하는 기간, 원하는 품목 등 조건에 맞춰<br>통계 지표를 조합하거나 비교할 수 있습니다.<br>일부 항목은 로그인 후 이용 가능합니다.',
            columns: [
                {
                    title: '맞춤통계',
                    items: ['<a href="https://stat.kita.net/stat/cstat/peri/tot/TotTotalList.screen">수출입 총괄</a>',
                     '<a href="https://stat.kita.net/stat/cstat/peri/item/ItemList.screen">품목 수출입</a>',
                     '<a href="https://stat.kita.net/stat/cstat/peri/ctr/CtrTotalList.screen">국가 수출입</a>',
                     '<a href="https://stat.kita.net/stat/cstat/peri/rel/RelTotalList.screen">대륙/경제권 수출입</a>']
                },
                {
                    title: '분석통계',
                    items: ['<a href="https://stat.kita.net/stat/cstat/anal/AnaCtrRanks.screen">순위통계</a>',
                     '<a href="https://stat.kita.net/stat/cstat/multy/MultyPeriodCtrList.screen">다중비교통계</a>']
                }
            ]
        },
        '자사통계': {
            title: '자사통계',
            desc: '자사의 수출입 실적을 확인하거나<br>수출의탑 포상이력을 확인해보세요.<br>KITA.net 로그인이 필요한 메뉴입니다.<br><br>유료회원사의 경우<br>올해 수출의탑 포상을 위한 시뮬레이션을<br>확인하실 수 있습니다.',
            columns: [
                {
                    title: '자사통계',
                    items: ['<a href="https://stat.kita.net/stat/cstat/corp/CorTotalList.screen">수출입 실적</a>',
                     '<a href="https://stat.kita.net/stat/cstat/awd/AwdHistoryList.screen">수출의탑 시뮬레이션</a>']
                }
            ]
        }
    };
   
   // ====== 상태 ======
   let submenuEl = null;              // 데스크톱용 플로팅 서브메뉴 엘리먼트
   let closeTimer = null;
   let hovering = false;
   let mode = null;                   // 'desktop' | 'mobile'
   const MQ = window.matchMedia('(min-width: 1025px)');

   const nav = document.querySelector('.main-navigation');
   const navLinks = () => Array.from(document.querySelectorAll('.main-navigation > a'));
   const header = document.querySelector('.header-container');
   const menuToggle = document.querySelector('.menu-toggle');
   const loginBtn = document.querySelector('.loginBtn');
   const kstatIcon = document.getElementById('kstatIcon');
   const boardBtn  = document.getElementById('board');

   // ====== 유틸 ======
   function clearSubmenu() {
     if (submenuEl) {
       submenuEl.remove();
       submenuEl = null;
     }
   }
   function setAllTopLinkColor(color = 'white') {
     navLinks().forEach(a => (a.style.color = color));
   }

   // ====== 공통 내비 이동(로고/게시판/로그인) ======
   if (kstatIcon) {
     kstatIcon.addEventListener('click', (e) => {
       e.preventDefault();
       window.location.href = '/';
     });
   }
   if (boardBtn) {
     boardBtn.addEventListener('click', (e) => {
       e.preventDefault();
       window.location.href = '/board/boardList';
     });
   }
   if (loginBtn) {
     loginBtn.addEventListener('click', (e) => {
       e.preventDefault();
       window.location.href = '/user/login';
     });
   }
         
   // --- 챗봇 iframe 관련 로직 ---
   const floatingChatIcon = document.querySelector(".floating-chat-icon");
   const chatbotContainer = document.getElementById("chatbot-iframe-container");
   const chatbotHeader    = document.getElementById("chatbot-drag-header");
   const chatbotCloseBtn  = document.getElementById("chatbot-close-btn");
   const chatbotHomeBtn   = document.getElementById("chatbot-home-btn");
   const chatbotIframe    = document.getElementById('chatbot-iframe');

   // --- 챗봇 상태 관리 (보이기/숨기기 & 페이지 이동시 유지) ---
   function showChatbot() {
     if (!chatbotContainer || !floatingChatIcon) return;
     chatbotContainer.style.display = 'flex';
     floatingChatIcon.style.display = 'none';
     sessionStorage.setItem('trit_chatbot_open', 'true');
   }

   function hideChatbot() {
     if (!chatbotContainer || !floatingChatIcon) return;
     chatbotContainer.style.display = 'none';
     floatingChatIcon.style.display = 'block';
     sessionStorage.setItem('trit_chatbot_open', 'false');
   }

   // 페이지 로드 시 챗봇 상태 복원
   if (sessionStorage.getItem('trit_chatbot_open') === 'true') {
     showChatbot();
   }

   // --- 이벤트 리스너 ---
   floatingChatIcon?.addEventListener("click", (e) => {
     e.preventDefault();
     showChatbot();
   });

   chatbotCloseBtn?.addEventListener('click', hideChatbot);
   
   chatbotHomeBtn?.addEventListener('click', () => {
     if (chatbotIframe) {
       // iframe을 리로드하여 초기 상태로 돌림
       chatbotIframe.contentWindow.location.reload();
     }
   });

   // iframe으로부터 메시지 수신
   window.addEventListener('message', (e) => {
     // 챗봇 창 닫기
     if (e.data?.type === 'TRIT_CLOSE') {
       hideChatbot();
     }
     // 챗봇 내부 콘텐츠 높이에 따라 iframe 높이 조절
     if (e.data?.type === 'TRIT_SIZE') {
       const h = Number(e.data.height);
       if (!Number.isNaN(h) && chatbotIframe) {
         chatbotIframe.style.height = h + 'px';
       }
     }
   });

   // --- 챗봇창 드래그 기능 ---
   let isDragging = false;
   let offsetX, offsetY;

   if (chatbotHeader && chatbotContainer) {
     chatbotHeader.addEventListener('mousedown', (e) => {
       isDragging = true;
       offsetX = e.clientX - chatbotContainer.offsetLeft;
       offsetY = e.clientY - chatbotContainer.offsetTop;
       if (chatbotIframe) chatbotIframe.style.pointerEvents = 'none';
     });

     document.addEventListener('mousemove', (e) => {
       if (!isDragging) return;
       let newX = e.clientX - offsetX;
       let newY = e.clientY - offsetY;

       // 화면의 3/4까지 밖으로 나갈 수 있도록 허용
       const chatbotWidth = chatbotContainer.offsetWidth;
       const chatbotHeight = chatbotContainer.offsetHeight;
       const minX = -chatbotWidth * 0.75;
       const maxX = window.innerWidth - (chatbotWidth * 0.25);
       const minY = 0; // 상단으로는 드래그 불가
       const maxY = window.innerHeight - (chatbotHeight * 0.25);

       newX = Math.max(minX, Math.min(newX, maxX));
       newY = Math.max(minY, Math.min(newY, maxY));

       Object.assign(chatbotContainer.style, {
         left: `${newX}px`,
         top: `${newY}px`,
         bottom: 'auto',
         right: 'auto',
       });
     });

     document.addEventListener('mouseup', () => {
       isDragging = false;
       if (chatbotIframe) chatbotIframe.style.pointerEvents = 'auto';
     });
   }
   
   
   
   // ====== 데스크톱 모드 ======
   function setupDesktop() {
     mode = 'desktop';
     // 햄버거 메뉴는 데스크톱에선 숨김(스타일에서 처리), 혹시 열려있으면 닫기
     nav.classList.remove('open');
     // 모바일에서 심어 둔 아코디언 서브메뉴 제거
     Array.from(nav.querySelectorAll('.mobile-submenu')).forEach(el => el.remove());
     Array.from(nav.querySelectorAll('.top-open')).forEach(el => el.classList.remove('top-open'));

     // 링크 이벤트 부착(hover로 열고, 떠나면 닫기 + 색 복구)
     navLinks().forEach(link => {
       const label = link.textContent.trim();

       const onEnter = () => {
         clearTimeout(closeTimer);
         setAllTopLinkColor('white');
         link.style.color = 'var(--text-accent)';

         clearSubmenu();
         const data = submenuData[label];
         if (!data) return;

         // 플로팅 서브메뉴 DOM
         const colsHTML = data.columns.map(col => `
           <div class="submenu-column">
             ${col.title ? `<h3 class="submenu-title">${col.title}</h3>` : ''}
             <ul>${col.items.map(item => `<li>${item}</li>`).join('')}</ul>
           </div>
         `).join('');

         submenuEl = document.createElement('div');
         submenuEl.className = 'submenu';
         submenuEl.innerHTML = `
           <div class="submenu-container">
             <div class="submenu-desc-area">
               <h2 style="font-size:30px;">${data.title}</h2>
               <p class="submenu-desc" style="color:#1B3C53;">${data.desc}</p>
             </div>
             <div class="submenu-columns">${colsHTML}</div>
           </div>
         `;

         document.body.appendChild(submenuEl);
         const rect = header.getBoundingClientRect();
         Object.assign(submenuEl.style, {
           position: 'fixed',
           top: `${rect.bottom}px`,
           left: '0',
           width: '100%',
         });

         hovering = true;
         submenuEl.addEventListener('mouseenter', () => (hovering = true));
         submenuEl.addEventListener('mouseleave', () => {
           hovering = false;
           closeTimer = setTimeout(() => {
             if (!hovering) {
               clearSubmenu();
               setAllTopLinkColor('white');
             }
           }, 120);
         });
       };

       const onLeave = () => {
         hovering = false;
         // 텍스트 색 즉시 복귀 (메뉴는 약간 지연 후 닫힘)
         link.style.color = 'white';
         closeTimer = setTimeout(() => {
           if (!hovering) {
             clearSubmenu();
             setAllTopLinkColor('white');
           }
         }, 120);
       };

       // 중복 바인딩 방지를 위해 먼저 제거 후 다시 등록
       link.removeEventListener('mouseenter', link._onEnter);
       link.removeEventListener('mouseleave', link._onLeave);
       link._onEnter = onEnter;
       link._onLeave = onLeave;
       link.addEventListener('mouseenter', onEnter);
       link.addEventListener('mouseleave', onLeave);
     });
   }

   // ====== 모바일 모드 ======
   function setupMobile() {
     mode = 'mobile';
     setAllTopLinkColor('white');
     clearSubmenu();

     if (menuToggle) {
       menuToggle.addEventListener('click', () => {
         nav.classList.toggle('show');
         // 뒤로가기 버튼 숨김 (처음에는 필요 없음)
         const backBtn = document.querySelector('.back-btn');
         if (backBtn) backBtn.remove();
       });
     }

     navLinks().forEach(link => {
       if (link._onClickMobile) link.removeEventListener('click', link._onClickMobile);

       const onClick = (e) => {
         e.preventDefault();
         const label = link.textContent.trim();
         const data = submenuData[label];
         if (!data) return;

         // 전체 메뉴 목록 숨기기
         Array.from(navLinks()).forEach(a => a.style.display = 'none');
         // link.style.display = 'block'; // 클릭한 메뉴만 보이게

         // 기존 서브메뉴 제거
         Array.from(nav.querySelectorAll('.mobile-submenu')).forEach(el => el.remove());

         // 서브메뉴 DOM 추가
         const colsHTML = data.columns.map(col => `
           <div class="mobile-submenu-section">
             ${col.title ? `<div class="mobile-submenu-title">${col.title}</div>` : ''}
             ${col.items.length ? `<ul class="mobile-submenu-list">
               ${col.items.map(item => `<li>${item}</li>`).join('')}
             </ul>` : ''}
           </div>
         `).join('');

         const panel = document.createElement('div');
         panel.className = 'mobile-submenu';
         panel.innerHTML =
           `<div class="mobile-submenu-desc">
                 <div class="mobile-submenu-heading">${data.title}</div>
              </div>` + colsHTML;
         link.after(panel);

         // 뒤로가기 버튼 추가
         let backBtn = document.createElement('button');
         backBtn.textContent = '← 뒤로가기';
         backBtn.className = 'back-btn';
         backBtn.style.margin = '10px';
         nav.prepend(backBtn);

         backBtn.addEventListener('click', () => {
           // 모든 상단 메뉴 다시 표시
           Array.from(navLinks()).forEach(a => a.style.display = 'block');
           // 서브메뉴 제거
           Array.from(nav.querySelectorAll('.mobile-submenu')).forEach(el => el.remove());
           // 뒤로가기 버튼 제거
           backBtn.remove();
         });
       };

       link._onClickMobile = onClick;
       link.addEventListener('click', onClick);
     });
   }

   // ====== 초기화 & 반응형 전환 ======
   function applyMode(e) {
     if (MQ.matches) setupDesktop();
     else setupMobile();
   }

   applyMode();
   MQ.addEventListener('change', applyMode);

   // 외부 클릭 시(데스크톱) 서브메뉴 닫기
   document.addEventListener('click', (e) => {
     if (mode !== 'desktop') return;
     if (!submenuEl) return;
     if (!submenuEl.contains(e.target) && !nav.contains(e.target)) {
       clearSubmenu();
       setAllTopLinkColor('white');
     }
   });

});