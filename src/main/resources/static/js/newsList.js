document.addEventListener("DOMContentLoaded", () => {

  /** -----------------------------
   * 1. 드롭다운 동작 + hidden input 값 반영
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
    options.forEach((option) => {
      option.addEventListener("click", () => {
        selectedText.textContent = option.textContent;
        hiddenCategory.value = option.dataset.value; // hidden input에 저장
        dropdown.classList.remove("active");
      });
    });

    // 외부 클릭 시 닫기
    document.addEventListener("click", (e) => {
      if (!dropdown.contains(e.target)) {
        dropdown.classList.remove("active");
      }
    });
   
   // 초기 선택값 표기: 서버가 넘긴 값(hiddenCategory.value)이 없으면 '전체'로 보이기
   const serverValue = (hiddenCategory.value || '').trim();
   if (!serverValue) {
     selectedText.textContent = '선택';
   } else {
     const match = Array.from(options).find(opt => opt.dataset.value === serverValue);
     selectedText.textContent = match ? match.textContent : '선택';
   }

  }

  /** -----------------------------
   * 2. 검색어 유지
   * ----------------------------- */
  const searchInput = document.querySelector('.search-input-wrapper input[name="searchKeyword"]');
  if (searchInput && searchInput.dataset.value) {
    searchInput.value = searchInput.dataset.value; // 서버에서 전달된 기존 검색어 유지
  }

});
