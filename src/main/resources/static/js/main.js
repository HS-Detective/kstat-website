// DOM이 완전히 로드된 후에 스크립트가 실행되도록 합니다.
document.addEventListener("DOMContentLoaded", function () {
  // class="trit-image-container" 클릭 시 /chat으로 이동
  const tritImageContainer = document.querySelector(".trit-image-container");
  if (tritImageContainer) {
    tritImageContainer.addEventListener("click", function () {
      // chat.html을 360x540 크기의 새 창으로 띄웁니다.
      window.open(
        "/chat",
        "TRIT_Chatbot",
        "width=360,height=540,scrollbars=yes,resizable=yes"
      );
    });
  }



  // class="news-card" 클릭 시 해당 뉴스 기사 링크로 이동
  const newsCards = document.querySelectorAll(".news-card");
  newsCards.forEach((card) => {
    card.addEventListener("click", function () {
      const newsLink = this.dataset.newsLink;
      if (newsLink && newsLink !== "${receivedLink}") {
        // 플레이스홀더가 아닐 때만 실행
        window.open(newsLink, "_blank"); // 새 탭에서 열기
      }
    });
  });

  // class="info-card PDF" 클릭 시 PDF 다운로드
  const pdfCard = document.querySelector(".info-card.PDF");
  if (pdfCard) {
    pdfCard.addEventListener("click", function () {
      const pdfLink = this.dataset.pdfLink;
      if (pdfLink) {
        const link = document.createElement("a");
        link.href = pdfLink;
        // 파일 이름만 추출하여 download 속성에 설정
        link.download = pdfLink.split("/").pop();
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      }
    });
  }
});
