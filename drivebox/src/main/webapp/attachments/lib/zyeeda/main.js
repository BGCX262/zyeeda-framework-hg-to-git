ZUI.call(function(Z) {
    Z.cfg = {
        uploader : {
            totalCountUrl : '/drivebox/rest/docs/count',
            uploadUrl : '/drivebox/rest/docs',
            listUrl : '/drivebox/rest/docs'
        }
    };

    ZYEEDA.com.zyeeda.zui.framework.demo.AttachmentDemo.main(Z);
});
