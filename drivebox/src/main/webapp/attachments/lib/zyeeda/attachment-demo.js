ZYEEDA.namespace('com.zyeeda.zui.framework.demo').AttachmentDemo = function() {

    return {
        main : function(Z) {
            var attach = new Z.AttachmentManager({
                totalCountUrl : Z.cfg.uploader.totalCountUrl,
                uploadUrl : Z.cfg.uploader.uploadUrl,
                listUrl : Z.cfg.uploader.listUrl,
                foreignId : 'tangrui'
            });
            attach.render('#attachment');

            var uploadedList = new Z.AttachmentUploadedList({
                listUrl : Z.cfg.uploader.listUrl,
                foreignId : 'zhaoqi',
                width : '800px',
                height : '335px',
                readOnly : true
            });
            uploadedList.render('#manager');

            Z.one('#destroyBtn').on('click', function() {
                uploadedList.destroy();
                attach.destroy();
            });
        }
    }

}();
