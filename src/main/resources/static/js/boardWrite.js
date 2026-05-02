document.addEventListener('DOMContentLoaded', function () {

  console.log("boardWrite.js loaded");
  
  // ===== DOM 요소 =====
  const titleInput = document.getElementById('boardTitle');        // 제목
  const contentInput = document.getElementById('boardContent');    // 본문
  const btnSubmit = document.getElementById('btn-submit');         // 등록 버튼
  const fileUploadBtn = document.getElementById('file-upload-btn');// "파일 선택" 버튼
  const fileUploadInput = document.getElementById('file-upload-input'); // 숨겨진 file input
  const fileNameDisplay = document.getElementById('file-name');    // 파일명 표시
  const btnList = document.getElementById('btn-list');             // 글목록 버튼
  const btnCancel = document.getElementById('btn-cancel');         // 취소 버튼
  const form = document.querySelector('.post-form');

  const TITLE_MAX = 50;
  const CONTENT_MAX = 2000;

  // ===== 파일 선택 버튼 클릭 → 숨겨진 file input 클릭 =====
  fileUploadBtn.addEventListener('click', () => {
    fileUploadInput.click();
  });

  // ===== 파일 선택 시 파일명 표시 =====
  fileUploadInput.addEventListener('change', function () {
    fileNameDisplay.textContent = this.files.length > 0 ? this.files[0].name : '선택된 파일 없음';
  });

  // ===== 글목록 버튼 =====
  btnList.addEventListener('click', (e) => {
    e.preventDefault();
    window.location.href = '/board/boardList';
  });

  // ===== 취소 버튼 =====
  btnCancel.addEventListener('click', () => {
    if (confirm('작성 중인 내용이 저장되지 않습니다. 목록으로 돌아가시겠습니까?')) {
      window.location.href = '/board/boardList';
    }
  });

  // ===== 등록 버튼 활성/비활성 제어 =====
  function updateSubmitState() {
    const title = titleInput.value.trim();
    const content = contentInput.value.trim();
    btnSubmit.disabled = !(title && content);
  }
  titleInput.addEventListener('input', updateSubmitState);
  contentInput.addEventListener('input', updateSubmitState);

  // ===== 폼 제출 시 유효성 검사 =====
  form.addEventListener('submit', function (e) {
    const title = titleInput.value.trim();
    const content = contentInput.value.trim();

    if (!title) {
      alert('제목을 입력해주세요.');
      e.preventDefault();
      return;
    }
    if (!content) {
      alert('본문을 입력해주세요.');
      e.preventDefault();
      return;
    }
    if (title.length > TITLE_MAX) {
      alert(`제목은 최대 ${TITLE_MAX}자까지 입력 가능합니다.`);
      e.preventDefault();
      return;
    }
    if (content.length > CONTENT_MAX) {
      alert(`본문은 최대 ${CONTENT_MAX}자까지 입력 가능합니다.`);
      e.preventDefault();
      return;
    }

    // 통과 → 서버 전송 (BoardDTO 매핑)
  });
  
     // ===== 수정 화면에서 ‘등록/수정’ 버튼 즉시 활성화 =====
  updateSubmitState();

});
