<#macro html>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="zh_CN" lang="zh_CN">
    <#nested>
</html>
</#macro>

<#macro yui_include filter="min">
    <script type="text/javascript" src="${CONTEXT_PATH}/static/yui/3.2.0/build/yui/yui-min.js"></script>
    <script type="text/javascript">
        YUI_config = {
            lang : 'zh-CN,en-US',
            base : '${CONTEXT_PATH}/static/yui/3.2.0/build/',
            charset : 'utf-8',
            loadOptional : true,
            combine : false,
            filter : '${filter}',
            timeout : 10000,
            groups : {
                yui2 : {
                    lang : 'zh-CN,en-US',
                    base : '${CONTEXT_PATH}/static/yui/2.8.2/build/',
                    combine : false,
                    patterns : { 
                        'yui2-' : {
                            configFn : function(me) {
                                if (/-skin|reset|fonts|grids|base/.test(me.name)) {
                                    me.type = 'css';
                                    me.path = me.path.replace(/\.js/, '.css');
                                    me.path = me.path.replace(/\/yui2-skin/, '/assets/skins/sam/yui2-skin');
                                }
                            }
                        } 
                    }
                }
            }
        };
    </script>
</#macro>

<#macro head title="中昱达">
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <title>${title}</title>
    <@yui_include />
    <#nested>
</head>
</#macro>
