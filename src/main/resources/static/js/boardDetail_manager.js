document.addEventListener("DOMContentLoaded", () => {
   console.log("boardDetail_manager.js loaded");

   // 공통 요소
   const MAX_REPLY = 1000;
   const btnList = document.querySelector(".btn-list");
   const btnDeletePost = document.querySelector(".delete-post-btn");
   const replyForm = document.querySelector(".reply-form");
   const replyInput = document.getElementById("replyContent");

   // boardSeq: hidden input > data-attr > URL 쿼리
   const getBoardSeq = () =>
      document.querySelector('input[name="boardSeq"]')?.value
      || btnDeletePost?.dataset.boardSeq
      || new URLSearchParams(location.search).get("boardSeq");

   // 게시글 삭제 
   btnDeletePost?.addEventListener("click", () => {
      const seq = getBoardSeq();
      if (seq && confirm("게시글을 삭제하시겠습니까?")) {
         location.href = `/board/boardDelete?boardSeq=${encodeURIComponent(seq)}`;
      }
   });

   // 답변 글자수 제한
   replyInput?.addEventListener("input", () => {
      if (replyInput.value.length > MAX_REPLY) {
         alert(`답변은 최대 ${MAX_REPLY}자까지 입력 가능합니다.`);
         replyInput.value = replyInput.value.slice(0, MAX_REPLY);
      }
   });

   // 답변 등록: 폼 기본 제출 + 최소 유효성
   replyForm?.addEventListener("submit", (e) => {
      const text = replyInput.value.trim();
      if (!text) {
         e.preventDefault();
         alert("답변 내용을 입력해주세요.");
      }
   });
   
   // 답변 수정 및 삭제
   document.addEventListener("click", async (e) => {
      const row = e.target.closest(".reply-row");
      const editBtn = e.target.closest(".edit-btn");
      const saveBtn = e.target.closest(".save-btn");
      const cancelBtn = e.target.closest(".cancel-btn");
      const delBtn = e.target.closest(".delete-btn");

      // 1) 수정 클릭 → 인라인 편집 모드로 전환
      if (editBtn && row) {
         if (row.dataset.editing === "1") return; // 중복 방지
         row.dataset.editing = "1";

         const contentCell = row.querySelector(".content-cell");
         const actionsCell = row.querySelector(".actions-cell");
         const original = (contentCell.textContent || "").trim();

         // 원문 보관
         contentCell.dataset.original = original;

         // 내용 셀을 textarea로 교체
         contentCell.innerHTML =
            `<textarea class="reply-edit-input" rows="6">${original}</textarea>`;

         // 액션 버튼 교체 (저장/취소)
         actionsCell.innerHTML =
            `<button type="button" class="btn btn-save save-btn">저장</button>
         <button type="button" class="btn btn-cancel cancel-btn">취소</button>`;

         // 커서 위치
         contentCell.querySelector("textarea").focus();
         return;
      }

      // 2) 취소 → 원래 보기 모드로 복원
      if (cancelBtn && row) {
         const contentCell = row.querySelector(".content-cell");
         const actionsCell = row.querySelector(".actions-cell");
         const original = contentCell.dataset.original || "";

         contentCell.textContent = original;
         actionsCell.innerHTML =
            `<button type="button" class="btn btn-edit edit-btn">수정</button>
         <button type="button" class="btn btn-delete delete-btn">삭제</button>`;
         delete row.dataset.editing;
         return;
      }

      // 3) 저장 → PUT /reply/update (JSON) 호출
      if (saveBtn && row) {
         const contentCell = row.querySelector(".content-cell");
         const textarea = contentCell.querySelector(".reply-edit-input");
         const newText = (textarea?.value || "").trim();

         if (!newText) return alert("답변 내용을 입력해주세요.");
         if (newText.length > MAX_REPLY) return alert(`답변은 1~${MAX_REPLY}자 이내여야 합니다.`);

         const boardSeq = getBoardSeq();
         if (!boardSeq) return alert("boardSeq를 찾을 수 없습니다.");

         try {
            const res = await fetch("/reply/update", {
               method: "PUT",
               headers: { "Content-Type": "application/json" },
               body: JSON.stringify({ boardSeq, replyContent: newText })
            });
            if (!res.ok) throw new Error(String(res.status));

            alert("답변이 수정되었습니다.");
            location.reload();
         } catch (err) {
            console.error(err);
            alert("수정 실패");
         }
         return;
      }

      // 4) 삭제
      if (delBtn) {
         if (!confirm("답변을 삭제하시겠습니까?")) return;
         const boardSeq = getBoardSeq();
         if (!boardSeq) return alert("boardSeq를 찾을 수 없습니다.");

         try {
            const res = await fetch(`/reply/delete?boardSeq=${encodeURIComponent(boardSeq)}`, {
               method: "DELETE"
            });
            if (!res.ok) throw new Error(String(res.status));
            alert("삭제되었습니다.");
            location.reload();
         } catch (err) {
            console.error(err);
            alert("삭제 실패");
         }
      }
   });
});