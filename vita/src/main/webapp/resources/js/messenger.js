

$(document).ready(function () {

    var sendMsgForm = $('#sendMsgForm');
    var messengerListDiv = $('#messengerList');
    var messengerSearchListDiv = $('#messengerSearchList');
    var messengerSearchForm = $('#msgSeachForm');
    var messengerListBtn = $('#msgListBtn');

    contactUser = $('#contactUser').val();

    messengerOpenFlag = true;   // 메신저 창 실행 여부

    messengerListDiv.empty();
    messengerService.getList(function (result) {
        messengerService.viewMessengerList(result);
        
        if (contactUser) {
            var select = messengerListDiv.find(`a[data-contact='${contactUser}']`).data('contact');
            // 기존 대화에 있는 유저인지 검사
            if(select) {
                select = messengerListDiv.find(`a[data-contact='${contactUser}']`);
                messengerService.selectContactUser(select);
            } else {
                messengerService.addMessenger(contactUser)
            }
        }
    });

    // 이벤트---------------------------------------------------------
    // 목록 선택
    messengerListDiv.on('click', 'a', function (event) {
        messengerService.selectContactUser(this);
        sendMsgForm.val('');
        messengerSearchForm.val('');
    });


    // 메세지 보내기
    // enter키
    sendMsgForm.on('keyup', function (event) {
        var msg = $(this).val().trim();
        if (event.keyCode == 13 && msg) {
            messengerService.sendMsg(msg);
            $(this).val('');
        }
    });

    // 보내기버튼
    $('#sendMsgBtn').on('click', function (event) {
        if (sendMsgForm.val().trim()) {
            messengerService.sendMsg(sendMsgForm.val());
            sendMsgForm.val('');
        }
    });


    //-------사용자 검색
    messengerSearchForm.on('keyup', function (event) {
        var search = $(this).val().trim();
        if (search) {
            messengerService.listAndSearchToggle(true);
            messengerService.search(search);
        } else {
            $(this).val('');
            messengerSearchListDiv.empty();
            messengerService.listAndSearchToggle(true);
        }
    });

    // 메신저 리스트, 검색리스트 토글
    messengerListBtn.on('click', function (event) {
        messengerService.listAndSearchToggle();
    });

    // 메신저 유저 추가
    messengerSearchListDiv.on('click', 'a', function (event) {
        messengerService.addMessenger($(this).data('contact'));
        messengerService.listAndSearchToggle();
        sendMsgForm.val('');
        messengerSearchForm.val('');
    });

});