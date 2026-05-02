$(function() {
   $("#login_button").on('click', login);
   $("#sign-up").on('click', signUp);
});

// 로그인 버튼 누르면 입력창 비었는지 확인 후 submit
function login() {
   let userId = $('#userId').val().trim();
   let userPwd = $('#userPwd').val().trim();
   
   if (userId.length < 1) {
      alert('아이디를 입력해주세요.');
      return;
   }
   
   if (userPwd.length < 1) {
      alert('비밀번호를 입력해주세요.');
      return;
   }
   
   $('#login-form').submit();
};

// 회원가입 버튼 누르면 회원가입 페이지로 이동
function signUp() {
   window.location.href = '/user/join';
};