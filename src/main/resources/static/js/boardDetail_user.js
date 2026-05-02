document.addEventListener('DOMContentLoaded', () => {
   
  /** 1. 글목록 이동 버튼 */
  const listBtn = document.querySelector('.btn-list');
  if (listBtn) {
      listBtn.addEventListener('click', (e) => {
        e.preventDefault();
        window.location.href = '/board/boardList';
      });
    }

  /** 2. 삭제 버튼 */
  const delBtn  = document.getElementById('btn-delete');   // ← 변수명 통일
    const delForm = document.getElementById('deleteForm');

    if (delBtn && delForm) {
      // 중복 바인딩 방지
      if (delBtn.dataset.bound !== '1') {
        delBtn.dataset.bound = '1';
        delBtn.addEventListener('click', (e) => {
          e.preventDefault();
          e.stopPropagation();
          if (confirm('정말 삭제하시겠습니까?')) {
            delForm.submit();
          }
        }, { once: true });
      }
    }
  });