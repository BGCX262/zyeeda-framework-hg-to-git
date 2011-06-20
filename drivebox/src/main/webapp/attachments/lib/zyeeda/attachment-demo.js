ZYEEDA.namespace('com.zyeeda.zui.framework.demo').AttachmentDemo = function() {

    return {
        main : function(Z) {
            var attach = new Z.Attachment({
                totalCountUrl : Z.cfg.uploader.totalCountUrl,
                uploadUrl : Z.cfg.uploader.uploadUrl,
                listUrl : Z.cfg.uploader.listUrl,
                foreignId : 'tangrui'
            });
            attach.render('#attachment');
        }
    }

}();
