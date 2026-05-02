document.addEventListener("DOMContentLoaded", () => {

  /** -----------------------------
   * 1. 상태 라벨 스타일 적용
   * ----------------------------- */
  document.querySelectorAll(".status-label").forEach(el => {
    const status = el.dataset.status;
    if (status === "진행중") {
      el.classList.add("status-inprogress");
      el.textContent = "진행중";
    } else if (status === "답변완료") {
      el.classList.add("status-complete");
      el.textContent = "답변완료";
    } else {
      el.textContent = status || "";
    }
  });

  /** -----------------------------
   * 2. 드롭다운 동작 + hidden input 값 반영
   * ----------------------------- */
  const dropdown = document.querySelector(".dropdown-select");
  const hiddenCategory = document.getElementById("searchCategory");
  const selectedText = document.querySelector(".selected-option");
  const options = document.querySelectorAll(".dropdown-menu li");

  if (dropdown && hiddenCategory && selectedText) {
    const toggleBtn = dropdown.querySelector(".dropdown-toggle");

    // 드롭다운 열기/닫기
    toggleBtn.addEventListener("click", (e) => {
      e.preventDefault();
      dropdown.classList.toggle("active");
    });

    // 항목 클릭 시 선택 반영
    options.forEach(option => {
      option.addEventListener("click", () => {
        selectedText.textContent = option.textContent;
        hiddenCategory.value = option.dataset.value; // hidden input에 저장
        dropdown.classList.remove("active");
      });
    });

    // 외부 클릭 시 닫기
    document.addEventListener("click", e => {
      if (!dropdown.contains(e.target)) {
        dropdown.classList.remove("active");
      }
    });
  }

  /** -----------------------------
   * 3. 검색어 유지
   * ----------------------------- */
  const searchInput = document.getElementById("searchKeyword");
  if (searchInput && searchInput.dataset.value) {
    searchInput.value = searchInput.dataset.value; // 서버에서 전달된 기존 검색어 유지
  }
  if (hiddenCategory && hiddenCategory.dataset.value) {
    // 기존 검색 카테고리에 맞춰 드롭다운 표시
    const match = Array.from(options).find(opt => opt.dataset.value === hiddenCategory.dataset.value);
    if (match) {
      selectedText.textContent = match.textContent;
      hiddenCategory.value = match.dataset.value;
    }
  }

  /** -----------------------------
   * 4. 글쓰기 버튼
   * ----------------------------- */
  const writeBtn = document.getElementById("btn-write");
  if (writeBtn) {
    writeBtn.addEventListener("click", () => {
      window.location.href = "/board/boardWrite";
    });
  }

});