function like(button, entityType, entityId) {
    $.post(
        CONTEXT_PATH + "/like",
        { "entityType":entityType,"entityId":entityId,
            function(data) {
                data = $.parseJSON(data);
                if(data.code == 0) {

                } else {
                    alert(data.msg);
                }
            }
        }
    );
}