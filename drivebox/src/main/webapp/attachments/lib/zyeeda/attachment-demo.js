ZYEEDA.namespace('framework.zui.demo').AttachmentDemo = function() {

};

ZYEEDA.framework.zui.demo.AttachmentDemo.prototype.main = function(Z) {
    var attach = new Z.Attachment({
        foreignId : 'tangrui',
        totalCountUrl : 'rest/docs/count',
        uploadUrl : '../../../../../../rest/docs/'
    });
    attach.render('#attachment');
}
