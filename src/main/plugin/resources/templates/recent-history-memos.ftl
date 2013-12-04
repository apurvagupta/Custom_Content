<html>
<head>
    <head>
        <#include '/template/global/include/recent-history-head.ftl'/>
    </head>
</head>
<body class="jive-body-history">

<#include '/template/global/include/recent-history-header.ftl' />

<!-- BEGIN main body -->
<div id="jive-body-main">

    <!-- BEGIN main body column -->
    <div id="jive-body-maincol-container">
        <div id="jive-body-maincol">


            <!-- BEGIN content list -->
            <div class="jive-content-block-container">
                <h3 class="jive-content-block-header"><@s.text name="recentHistory.memo.tooltip"/></h3>
                
                <div class="jive-content-block">
                    <div id="jive-history-content">
                        <div id="jive-memo-content">
                            <div class="jive-table">
                                <#assign memos = memoHistory.iterator()>
                                <#if (!memos?exists || !memos.hasNext())>
                                    <div class="jive-recentcontent-none">
                                        <p><@s.text name="recentHistory.memo.empty" /></p>
                                    </div>
                                <#else>
                                    <#include "/plugins/memo-type/resources/templates/memo-macros.ftl">
                                    <@memoList memos=memos />
                                </#if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>