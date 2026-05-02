let idCheck = false;
let pwdCheck = false;
let emailCheck = false;

$(function() {
  $("#userId").on('keyup', confirm);
  $("#pwdConfirm").on('keyup', confirmPwd)
  $("#email").on('keyup', confirmEmail)
  $("#joinBtn").on('click', join);
});

function join() {
  
   let userId = $('#userId').val().trim();
    let userPwd = $('#userPwd').val().trim();
   let pwdConfirm = $('#pwdConfirm').val().trim();
    let userName = $('#userName').val().trim();
    let email = $('#email').val().trim();
   
    if(userId.length < 1 || userPwd.length < 1 || userName.length < 1 || email.length < 1) {
      alert("입력창을 모두 채워주세요.");
      return;
    }
   
   if (!idCheck) return;
   if (!pwdCheck) return;
   if (!emailCheck) return;
   
   $('#join-form').submit();
}

function confirmEmail(){
   const regex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/;
   let email = $('#email').val().trim();
   
   if(!regex.test(email)) {
      $('#confirmEmail').css({ 'color': 'red', 'font-size': '0.8em' });
      $('#confirmEmail').html('이메일 형식을 확인하세요.');
      emailCheck=false;
   } else {
      $('#confirmEmail').css({ 'color': 'blue', 'font-size': '0.8em' });
      $('#confirmEmail').html('알맞은 이메일 형식입니다.');
      emailCheck=true;
   }
}

function confirmPwd(){
   let userPwd = $('#userPwd').val().trim();
   let pwdConfirm = $('#pwdConfirm').val().trim();
   
   if(userPwd != pwdConfirm) {
      $('#confirmPwd').css({ 'color': 'red', 'font-size': '0.8em' });
      $('#confirmPwd').html('비밀번호와 다릅니다.');
      pwdCheck=false;
   } else {
      $('#confirmPwd').css({ 'color': 'blue', 'font-size': '0.8em' });
      $('#confirmPwd').html('비밀번호 확인 완료');
      pwdCheck=true;
   }
      
}


function confirm() {
  let userId = $('#userId').val().trim();

  // 아이디 길이 유효성 검사
  if (userId.length < 3 || userId.length > 5) {
    $('#confirmId').css({ 'color': 'red', 'font-size': '0.8em' });
    $('#confirmId').html('아이디는 3~5자 이내로 입력하세요.');
    return;
  }
  
  // 아이디 중복 체크
  $.ajax({
    url: '/user/confirmId',
    method: 'POST',
    data: {"userId": userId},
    success: function(resp) {
     console.log("중복확인 결과:", resp);
      if (resp) {
        $('#confirmId').css({ 'color': 'blue', 'font-size': '0.8em' });
        $('#confirmId').html('사용 가능한 아이디입니다.');
        idCheck = true;
      } else {
        $('#confirmId').css({ 'color': 'red', 'font-size': '0.8em' });
        $('#confirmId').html('이미 사용중인 아이디입니다.');
        idCheck = false;
      }
    }
  }); 

} 