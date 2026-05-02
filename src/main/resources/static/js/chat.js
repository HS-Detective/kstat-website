document.addEventListener("DOMContentLoaded", () => {
  const $ = (sel, root = document) => root.querySelector(sel);
  const $$ = (sel, root = document) => Array.from(root.querySelectorAll(sel));

  const state = {
    mode: window.TRIT_BOOT?.mode || "main",
    sessionId: window.TRIT_BOOT?.sessionId || "",
    menuOpen: false,
    history: [], // [{role:'human'|'ai', content:'...'}]
  };

  const MAX_TURNS = 10;
  state.history = state.history.slice(-2 * MAX_TURNS); // human/ai 2개 = 1턴

  const introEl = $("#intro");
  const historyEl = $("#history"); // ← 이름 변경
  const bodyEl = $("#chatBody");
  const inputEl = $("#chatInput");
  const charCountEl = $("#charCount");
  const menuToggle = $("#menuToggle");
  const menuOverlay = $("#menuOverlay");
  const closeBtn = $("#closeBtn");
  const sendBtn = $("#sendBtn");
  const chatFooter = $(".chat-footer");
  const chatbotHomeBtn = document.getElementById("chatbot-home-btn");
  const detailModal = $("#detail-modal");
  const detailModalBody = $("#detail-modal-body");
  const detailModalClose = $("#detail-modal-close");
  const popupData = {
    "countries-list": {
      title: "상위 20개국 (수출입 기준)",
      content: `
      <p>K-stat에서 제공하는 상위 20개국 목록입니다.</p>
      <ul>
        <li>미국</li><li>중국</li><li>베트남</li><li>대만</li><li>홍콩</li><li>일본</li>
        <li>인도(인디아)</li><li>싱가포르</li><li>호주</li><li>멕시코</li><li>말레이시아</li>
        <li>필리핀</li><li>캐나다</li><li>독일</li><li>폴란드</li><li>튀르키예</li>
        <li>네덜란드</li><li>태국</li><li>인도네시아</li><li>영국</li> `,
    },
    "products-list": {
      title: "상위 20개 품목 (수출입 기준)",
      content: `
      <p>K-stat에서 제공하는 상위 20개 품목 목록입니다.</p>
      <ul>
        <li>집적회로반도체</li><li>승용차</li><li>선박</li><li>합성수지</li><li>자동차부품</li>
        <li>기타자동차</li><li>경유</li><li>평판디스플레이</li><li>의약품</li><li>화장품</li>
        <li>기타정밀화학원료</li><li>휘발유</li><li>전산기록매체</li><li>무선통신기기부품</li>
        <li>전기자동차</li><li>제트유및등유</li><li>기초유분</li><li>축전지</li>
        <li>기타플라스틱제품</li><li>아연도강판</li>
      `,
    },
  };

  // 닫기 버튼
  closeBtn?.addEventListener("click", () => {
    try {
      window.close();
    } catch (e) {}
    if (window.parent && window.parent !== window) {
      window.parent.postMessage({ type: "TRIT_CLOSE" }, "*");
    }
  });

    // 6개 카드: 반드시 $ 사용
  $$(".action-card").forEach((a) => {
    a.addEventListener("click", async (e) => {
      const mode = a.dataset.mode;

      // data-mode가 있는 버튼만 챗봇 로직을 실행
      if (mode) {
        e.preventDefault();
        toChatMode(mode);

        const newSession = a.dataset.newSession === "true";
        if (mode !== state.mode || newSession) {
          try {
            await startNewSession(mode);
            seedIntroMessage(mode);
          } catch {
            addBot("세션을 시작하지 못했어요. 잠시 후 다시 시도해 주세요.");
          }
        }
      }
      // data-mode가 없는 링크는 리스너가 아무것도 하지 않으므로 기본 동작대로 링크가 열림
    });
  });

  // 메뉴 토글
  menuToggle?.addEventListener("click", () => toggleMenu());

  // 채팅창 클릭 시 메뉴 닫기
  bodyEl?.addEventListener("click", () => {
    if (state.menuOpen) {
      toggleMenu(false);
    }
  });

  menuOverlay?.addEventListener("click", async (e) => {
    const btn = e.target.closest(".menu-button");
    if (!btn) return;
    const mode = btn.dataset.mode;
    if (!mode) return;

    toChatMode(mode);

    const newSession = btn.dataset.newSession === "true";
    if (mode !== state.mode || newSession) {
      try {
        await startNewSession(mode);
        seedIntroMessage(mode);
      } catch {
        addBot("세션을 시작하지 못했어요. 잠시 후 다시 시도해 주세요.");
      }
    }
    toggleMenu(false);
  });

  historyEl?.addEventListener("click", (e) => {
    const trigger = e.target.closest(".detail-popup-trigger");
    if (!trigger) return;

    e.preventDefault();
    const popupId = trigger.dataset.popupId;
    const data = popupData[popupId];

    if (data && detailModalBody && detailModal) {
      detailModalBody.innerHTML = `<h3>${data.title}</h3>${data.content}`;
      detailModal.hidden = false;
    }
  });

  detailModalClose?.addEventListener("click", () => {
    if (detailModal) {
      detailModal.hidden = true;
    }
  });

  // 오버레이 클릭 시 닫기
  detailModal?.addEventListener("click", (e) => {
    if (e.target === detailModal) {
      detailModal.hidden = true;
    }
  });

  function toggleMenu(force) {
    state.menuOpen = typeof force === "boolean" ? force : !state.menuOpen;
    if (menuOverlay) menuOverlay.hidden = !state.menuOpen;
  }

  function autoResize() {
    if (!inputEl) return;
    inputEl.style.height = "auto";
    inputEl.style.height = Math.min(inputEl.scrollHeight, 60) + "px";
  }

  inputEl?.addEventListener("input", () => {
    if (charCountEl && inputEl)
      charCountEl.textContent = String(inputEl.value.length);
    autoResize();
  });

  // 키보드 이벤트 리스너 (엔터 입력 시 메시지 전송)
  inputEl?.addEventListener("keydown", (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  });

  // 버튼 이벤트 리스너
  sendBtn?.addEventListener("click", () => {
    sendMessage();
  });

  // 새로운 세션 시작 시 초기화
  async function startNewSession(mode) {
    const res = await fetch(`/api/chat/${mode}/new-session`, {
      method: "POST",
    });
    const data = await res.json();
    state.sessionId = data.sessionId;
    state.mode = mode;
    state.history = [];
    if (historyEl) historyEl.innerHTML = "";
  }

  function toChatMode(mode) {
    introEl?.setAttribute("hidden", "true");
    historyEl?.removeAttribute("hidden");
    chatFooter?.removeAttribute("hidden");
    try {
      window.history.pushState({ mode }, "", `#${mode}`); // ← 전역 history 사용
    } catch {}
    inputEl?.focus();
  }

  function seedIntroMessage(mode) {
    const map = {
      hs: `HS code  추천을 도와드릴 Trit입니다. 😊

현재 추천 시스템은
- 33류는 HS 10자리 코드
- 그 외 1~32, 34~97류는 HS 6자리까지만 제공됩니다.

코드가 자세할수록 더 정확한 추천이 가능하지만,
추천 결과는 참고용이며 실제와 다를 수 있어요.
제품명만 입력하면 추천 정확도가 낮아질 수 있으니,
**용도나 소재**도 함께 알려주세요!

예: “자동차 바디용 알루미늄 소재”`,
      stats: `무역 통계 안내를 도와드릴 Trit입니다. 😊

현재는 K-stat 기준으로
- 상위 20개국 (국가 수출입 기준)
   > 20개국
- 상위 20개 품목 (MTI 4단위 기준)
   > 20개 품목
- 최근 3개년(2022~2024년) 수출입 통계만 안내해 드리고 있어요.

※ 제공 범위는 점차 확대해 나갈 예정입니다.

국가, 품목, 기간을 함께 입력해 주세요!
예: “2024년 중국의 반도체 수출액은?”`,
      nav: `메뉴 경로 안내를 도와드릴 Trit입니다. 😊

찾고 싶은 국가나 품목, 연도를 말씀해 주세요.
K-stat에서 해당 정보를 어디서 확인할 수 있는지
메뉴 경로와 바로 가기 링크를 안내해 드릴게요!

예: “일본 수출입 통계 보고 싶어요”`,
      glossary: `무역 용어 설명을 도와드릴 Trit입니다. 😊

무역이나 수출입 용어, 어렵게 느껴지셨다면?
챗봇이 간단하게 설명해 드릴게요!

예:“FOB가 뭐예요?”
“CIF 뜻 알려줘”
“무역수지란?”`,
      faq: `K-Stat FAQ 안내를 도와드릴 Trit입니다. 😊

‘무쉽따’ 자료를 바탕으로
K-Stat 이용 중 자주 묻는 질문에 대해
챗봇이 간단하게 답변해 드릴게요!

예: “데이터는 언제 업데이트돼요?”
“코드 연계표가 뭐예요?”
“회원 전용 메뉴는 어디인가요?”`,
    };
    if (map[mode]) addBot(map[mode]);
    scrollToBottom();
  }

  function addUser(text) {
    if (!historyEl) return;
    const item = document.createElement("div");
    item.className = "chat-message user";
    item.innerHTML = `<div class="message-bubble"><p>${formatMsg(
      text
    )}</p></div>`;
    historyEl.appendChild(item);
  }
  function addBot(text) {
    if (!historyEl) return;
    const item = document.createElement("div");
    item.className = "chat-message bot";
    item.innerHTML = `
      <div class="chat-avatar"><img src="/images/trit_icon.png" alt="avatar"></div>
      <div class="message-bubble"><p>${formatMsg(text)}</p></div>
    `;
    historyEl.appendChild(item);
  }
  function scrollToBottom() {
    if (historyEl) historyEl.scrollTop = historyEl.scrollHeight;
  }

  async function sendMessage() {
    if (!inputEl) return;
    const text = inputEl.value.trim();
    if (!text) return;
    addUser(text);
    state.history.push({ role: "human", content: text });
    inputEl.value = "";
    if (charCountEl) charCountEl.textContent = "0";
    autoResize();
    scrollToBottom();

    const loading = document.createElement("div");
    loading.className = "chat-message bot";
    loading.innerHTML = `
      <div class="chat-avatar"><img src="/images/trit_icon.png" alt="avatar"></div>
      <div class="message-bubble"><p>입력중 . . .</p></div>`;
    historyEl?.appendChild(loading);
    scrollToBottom();

    try {
      const res = await fetch(`/api/chat/${state.mode}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          sessionId: state.sessionId,
          question: text,
          chat_history: state.history,
        }),
      });
      historyEl?.removeChild(loading);
      if (!res.ok) {
        addBot("오류가 발생했어요. 잠시 후 다시 시도해 주세요.");
        return;
      }
      const data = await res.json();
      addBot(data.reply || "(응답 없음)");
      if (data.chat_history) {
        state.history = data.chat_history;
      }
    } catch (err) {
      historyEl?.removeChild(loading);
      addBot("네트워크 오류가 발생했어요.");
    }
    scrollToBottom();
  }

  function escapeHtml(s) {
    return s.replace(
      /[&<>\""]/g,
      (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" }[c])
    );
  }
  function nl2br(s) {
    return s.replace(/\n/g, "<br>");
  }

  // --- 최종 초기화 ---
  // 저장된 상태에 따라 UI 복원
  if (state.mode && state.mode !== "main") {
    toChatMode(state.mode);
    renderHistory(); // 저장된 대화내역 복원
    scrollToBottom();
  } else {
    // 페이지가 새로 로드되었지만, 이전 대화가 없는 경우
    // 사이즈 리포트를 해서 iframe 높이를 맞춘다.
    reportSize();
  }

  // iframe 높이 조절을 위해 부모 창에 메시지 전송
  function reportSize() {
    const h = Math.max(
      document.body.scrollHeight,
      document.documentElement.scrollHeight
    );
    if (window.parent) {
      window.parent.postMessage({ type: "TRIT_SIZE", height: h }, "*");
    }
  }
  window.addEventListener("load", reportSize);
  new ResizeObserver(reportSize).observe(document.documentElement);

  // 1) [텍스트](https://example.com) 마크다운 링크 지원(선택)
  function linkifyMarkdown(safeHtml) {
    return safeHtml.replace(
      /\[([^\]]+)\]\((https?:\/\/[^\s)]+)\)/g,
      (m, text, url) =>
        `<a href="${url}" target="_blank" rel="noopener noreferrer nofollow">${text}</a>`
    );
  }

  // 2) 그냥 적은 URL도 자동 링크 (http/https, www.)
  function linkifyUrls(safeHtml) {
    return safeHtml.replace(
      /(?<!href=["'][^"']*?)((https?:\/\/|www\.)[^\s<>"']+)/gi,
      (m) => {
        // 끝에 따라붙는 문장부호는 링크 밖으로 뺌
        const trailMatch = m.match(/[),.;:!?]+$/);
        const trail = trailMatch ? trailMatch[0] : "";
        const core = trail ? m.slice(0, -trail.length) : m;

        const href = core.startsWith("http") ? core : `http://${core}`;
        return `<a href="${href}" target="_blank" rel="noopener noreferrer nofollow">${core}</a>${trail}`;
      }
    );
  }

  // 3) 팝업 링크 변환
  function linkifyPopups(safeHtml) {
    let html = safeHtml;
    html = html.replace(
      /(\s*&gt;\s+)(20개국)/g,
      '$1<a href="#" class="detail-popup-trigger" data-popup-id="countries-list">$2</a>'
    );
    html = html.replace(
      /(\s*&gt;\s+)(20개 품목)/g,
      '$1<a href="#" class="detail-popup-trigger" data-popup-id="products-list">$2</a>'
    );
    return html;
  }

  // 4) 메시지 포맷 파이프라인: escape → (md링크) → URL링크 → 개행
  function formatMsg(raw) {
    let s = escapeHtml(raw); // XSS 방지
    s = linkifyMarkdown(s); // 선택 기능
    s = linkifyUrls(s); // 필수 기능
    s = linkifyPopups(s); // 팝업 기능
    s = nl2br(s); // 줄바꿈 유지
    return s;
  }
});
