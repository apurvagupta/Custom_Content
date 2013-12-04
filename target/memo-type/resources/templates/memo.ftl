<html>
<head>
    <title>${memo.subject?html}</title>

    <style type="text/css" media="screen">
        @import "<@s.url value='/styles/jive-wiki.css'/>";
        @import "<@s.url value='/styles/jive-people.css'/>";
    </style>
    
    <#-- Include JavaScript Library and RTE -->
    <@resource.javascript header="true">
        ${macroJavaScript}
    </@resource.javascript>
    <#include "/template/global/include/comment-macros.ftl" />
    <#include "/template/global/include/tag-macros.ftl" />
    <@resource.dwr file="WikiTextConverter" />
    <@resource.dwr file="Comment" />

    <content tag="breadcrumb">
        <@s.action name="community-breadcrumb" executeResult="true" ignoreContextParams="true">
            <@s.param name="containerType" value="${container.objectType?c}" />
            <@s.param name="container" value="${container.ID?c}" />
        </@s.action>
        <a href="<@s.url value='${JiveResourceResolver.getJiveObjectURL(container)}'/>?view=memo" class="jive-link-more"><@s.text name="memo.main.brdcrmb.memos.link" /></a>
    </content>

    <#if watchable>
    <#assign memoStartWatchDescText>
        <@s.text name="memo.start_watch_desc.text" />
    </#assign>
    <#assign memoStopWatchDescText>
        <@s.text name="memo.stop_watch_desc.text" />
    </#assign>
    <@resource.dwr file="MemoWatches" />
    <@resource.javascript>
            function startWatch() {
                MemoWatches.watchMemo(memoId, {
                        callback:function(){
                            $('jive-memo-startWatch').hide();
                            $('jive-memo-stopWatch').show();
                            Jive.AlertMessage('thread.watch.notify', {
                                    beforeStart:function() {
                                        $('thread.watch.notify').innerHTML
                                            = '<div>${memoStartWatchDescText?js_string}</div>';
                                    }
                             });
                        }
                });
            }

            function stopWatch() {
                MemoWatches.removeMemoWatch(memoId, {
                        callback:function(){
                            $('jive-memo-startWatch').show();
                            $('jive-memo-stopWatch').hide();
                            Jive.AlertMessage('thread.watch.notify', {
                                    beforeStart:function() {
                                        $('thread.watch.notify').innerHTML
                                            = '<div>${memoStopWatchDescText?js_string}</div>';
                                    }
                             });
                        }
                });
            }
    </@resource.javascript>
    </#if>

    <#assign memoDeleteConfirmDelMemoText>
        <@s.text name="memo.delete.confirm_del_memo.text" />
    </#assign>
    <script type="text/javascript">
        var memoId = ${memo.ID?c};

        function deleteMemo() {
            if (confirm('${memoDeleteConfirmDelMemoText?js_string}')){
                $('memoDeleteForm').submit();
            }
        }
    </script>
</head>

<@jive.featureContentObject objectType=memo.objectType?c objectID=memo.ID?c containerType=container.objectType?c containerID=container.ID?c/>
<body class="jive-body-content jive-body-content-memo">

    <form id="memoDeleteForm" method="post" action='<@s.url value="/delete-memo.jspa?memo=${memo.ID?c}" />'>
        <@jive.token name="memo.delete.${memo.ID}" />
    </form>

    <!-- BEGIN main body -->
    <div id="jive-body-main">

        <!-- BEGIN main body column -->
        <div id="jive-body-maincol-container">

            <div id="jive-body-maincol">
                <div id="thread.watch.notify" class="jive-info-box" style="display:none"></div>
            
                <div id="content-featured-notify" class="jive-info-box" style="display:none"></div>
                <#include "/template/global/include/form-message.ftl"/>
            
	            <#if memo.status == enums['com.jivesoftware.community.JiveContentObject$Status'].AWAITING_MODERATION || memo.status == enums['com.jivesoftware.community.JiveContentObject$Status'].ABUSE_HIDDEN>
    	            <#assign moderationClassName> jive-memo-moderated</#assign>
        	    </#if>
            
                <div class="jive-memo ${moderationClassName!?html}">
                    <div class="jive-memo-title">
                        <div class="jive-memo-title-content">
         	          		<div class="jive-memo-moderating">
                        		<span class="jive-icon-med jive-icon-moderation"></span><@s.text name="mod.post_in_moderation.text" />
                            </div>
                            <h2>${memo.subject?html}</h2>
                        </div>
                    </div>
                    <div class="jive-memo-body">
	                    <ul>
	                        <li>
	                            <strong><@s.text name="memo.description.label" /><@s.text name="global.colon" /></strong>
	                            <span>${action.renderToHtml(memo)}</span>
	                        </li>
	                    </ul>
	                    
                        <div class="jive-memo-tags">
                            <#assign allObjectTagNames = "">
                            <#assign objectTagSets = action.getObjectTagSets(memo)>
                            <#if (objectTagSets.hasNext()) >
                            <span class="jive-content-footer-item">
                                <span class="jive-icon-med jive-icon-folder"></span><@s.text name="global.categories" /><@s.text name="global.colon" />
                                <#list objectTagSets as tagSet> ${tagSet.name}<#if objectTagSets.hasNext()>, </#if></#list>
                            </span>
                            </#if>
                    
                    
                            <#if (tags.hasNext())>
                            <span class="jive-content-footer-item">
                                <span class="jive-icon-sml jive-icon-tag"></span><@s.text name="global.tags" /><@s.text name="global.colon" />
                                <#list tags as tag>
                                    <a href="<@s.url value='${JiveResourceResolver.getJiveObjectURL(memo.jiveContainer)}'/>?view=tags&tags=${tag.name?url}">${action.renderTagToHtml(tag)}</a><#if (tag_has_next)>,</#if>
                                </#list>
                            </span>
                            </#if>
                        </div>
	            	</div>
                </div>
                
                <@comments contentObject=memo />

            </div>
        </div>
        <!-- END main body column -->

        <!-- BEGIN sidebar column -->
        <div id="jive-body-sidebarcol-container">

            <div id="jive-body-sidebarcol">

                <#include "/plugins/memo-type/resources/templates/memo-sidebar.ftl" />

            </div>

        </div>
        <!-- END sidebar column -->


    </div>

</body>
</html>