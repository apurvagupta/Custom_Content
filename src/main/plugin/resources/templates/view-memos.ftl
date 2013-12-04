<#include "/plugins/memo-type/resources/templates/memo-macros.ftl">

<@resource.javascript file="/resources/scripts/jquery/jquery.scrollTo.js" />
<@resource.javascript file="/resources/scripts/vendor/sammy/lib/sammy.js" />
<@resource.javascript file="/resources/scripts/lib/core_ext/object.js" />
<@resource.javascript file="/resources/scripts/lib/core_ext/array.js" />
<@resource.javascript file="/resources/scripts/apps/pager/models/parameter.js" />
<@resource.javascript file="/resources/scripts/apps/pager/views/main_view.js" />
<@resource.javascript file="/resources/scripts/apps/pager/main.js" />
<@resource.javascript file="/resources/scripts/jquery/jquery.livequery.js" />
<@resource.javascript file="/resources/scripts/apps/autosubmitter.js" />
<@resource.javascript file="/resources/scripts/apps/mark_all_read.js" />
<@resource.javascript>
    $j(document).ready(function() {
        var pager = new jive.Pager.Main($j('#jive-memo-content-block-container').parent(),
                                        "<@s.url action='view-memos' />",
                                        {
                                            <#if container?exists>
                                            containerType: ${container.objectType?c},
                                            containerID:   ${container.ID?c},
                                            </#if>
                                            <#if targetUser?exists>
                                            targetUser: ${targetUser.ID?c},
                                            </#if>
                                            <#if numResults?exists>
                                            per_page: ${numResults?c}
                                            <#else>
                                            per_page: 30
                                            </#if>
                                        });
        if (typeof(ContentFilterHandler) != 'undefined') {
            ContentFilterHandler.contentLoader = pager;
        }
    });
</@resource.javascript>

<!--BEGIN featured-content table-->
<#if action.hasFeaturedContent(container, statics['com.jivesoftware.community.ext.memo.MemoObjectType'].MEMO_TYPE_ID)>
<div class="jive-box jive-box-featured">
    <div class="jive-box-header">
        <h4><@s.text name="featuredcontent.memos.title" /></h4>
    </div>
    <div class="jive-box-body">
        <div class="jive-featured">
        <#list action.getFeaturedContent(container, statics['com.jivesoftware.community.ext.memo.MemoObjectType'].MEMO_TYPE_ID) as memo>
            <div class="jive-featured-item">
                <a href="<@s.url value='${JiveResourceResolver.getJiveObjectURL(memo)}'/>" ><span class="${SkinUtils.getJiveObjectCss(memo, 1)}"></span>${action.renderSubjectToText(memo)}</a>
                <span class="font-color-meta-light"><@s.text name="settings.by.text" /> <@jive.userDisplayNameLink user=memo.user/></span>
            </div>
        </#list>
        </div>
    </div>
</div>
</#if>
<!-- END featured-content table-->

<!-- BEGIN content list -->
<div class="jive-content-block-container" id="jive-memo-content-block-container">
    <#if targetUser?exists && !targetUser.anonymous>
        <#if (!user.anonymous && user.ID == targetUser.ID)>
            <div class="jive-box-header"><h4><@s.text name="settings.yourMemos.gtitle" /></h4></div>
            <span class="jive-content-block-description">
                <p><@s.text name="settings.blIsListOfYrMemos.text" /></p>

            </span>
        <#else>
            <div class="jive-box-header"><h4><@s.text name='settings.usrsMemos.gtitle'><@s.param><@jive.displayUserDisplayName user=targetUser/></@s.param></@s.text></h4></div>
            <span class="jive-content-block-description"><p><@s.text name='settings.blAreAllUsrsMemos.text'><@s.param><@jive.displayUserDisplayName user=targetUser /></@s.param></@s.text></p></span>
        </#if>
    <#else>
        <div class="jive-box-header"><h4><@s.text name="community.cont.memos.gtitle" /></h4></div>
    </#if>

    <div class="jive-content-block">
        <#if !(targetUser?exists)>
        <#assign tagSets = action.getTagSets(container)>
        <#assign tsObjectType = statics['com.jivesoftware.community.ext.memo.MemoTagSetObjectType'].getInstance()>
        <#include '/template/global/category-display.ftl' />
        </#if>

        <!-- BEGIN content results -->
        <div id="jive-content-results">

            <div id="jive-memo-content">
                
                <#if ((memos?exists && memos.hasNext()))>
                <div class="jive-box-controls clearfix">
                    <!-- BEGIN topics per page dropdown -->
                    <form action="#/page_sizes/" method="get" class="autosubmit">
                        <span class="jive-items-per-page">
                            <select name="numResults" id="jiveviewmemosform-numresults">
                                <option value="15" <#if numResults == 15>selected="selected"</#if>>15</option>
                                <option value="30" <#if numResults == 30>selected="selected"</#if>>30</option>
                                <option value="50" <#if numResults == 50>selected="selected"</#if>>50</option>
                            </select>
                            <span><@s.text name="community.items_per_page.label" /></span>
                        </span>
                    </form>
                    <!-- END topics per page dropdown -->
        
                    <#assign paginator = newPaginator>
                    <#if (paginator.numPages > 1)>
                    <!-- BEGIN pagination-->
                    <span class="jive-pagination">
                        <span class="jive-pagination-numbers">
                            <#list paginator.getPages(3) as page>
                            <#if (page?exists)>
                                <a href="#/pages/${page.number}" <#if (page.start == start)>class="jive-pagination-current"</#if> >
                                    ${page.number}
                                </a>
                            <#else>
                                <span>...</span>
                            </#if>
                            </#list>
                        </span>
                        <span class="jive-pagination-prevnext">
                            <#if (paginator.previousPage)>
                                <a href="#/pages/${paginator.getPageIndex()}" class="jive-pagination-prev">
                                    <@s.text name="global.previous"/>
                                </a>
                            <#else>
                                <span class="jive-pagination-prev-none"><@s.text name="global.previous"/></span>
                            </#if>
                            <#if (paginator.nextPage)>
                                <a href="#/pages/${paginator.getPageIndex() + 2}" class="jive-pagination-next">
                                    <@s.text name="global.next"/>
                                </a>
                            <#else>
                                <span class="jive-pagination-next-none"><@s.text name="global.next"/></span>
                            </#if>
                        </span>
                    </span>
                    <!-- END pagination -->
                    </#if>
                </div>
                </#if>
             
                <#if (!memos?exists || !memos.hasNext())>
                <@jive.jiveEmptyContentList container=container showTypeExclusively="memo"/>
                <#else>
                <div class="jive-table">   
                    <@memoList memos=memos/>
                </div>
                
                <!-- BEGIN table footer -->
                <div class="jive-box-controls jive-box-footer clearfix">

                    <#assign paginator = newPaginator>
                    <#if (paginator.numPages > 1)>
                        <!-- BEGIN pagination-->
                        <span class="jive-pagination">
                            <span class="jive-pagination-numbers">
                                <#list paginator.getPages(3) as page>
                                <#if (page?exists)>
                                    <a href="#/pages/${page.number}" <#if (page.start == start)>class="jive-pagination-current"</#if> >
                                        ${page.number}
                                    </a>
                                <#else>
                                    <span>...</span>
                                </#if>
                                </#list>
                            </span>
                            <span class="jive-pagination-prevnext">
                                <#if (paginator.previousPage)>
                                    <a href="#/pages/${paginator.getPageIndex()}" class="jive-pagination-prev">
                                        <@s.text name="global.previous"/>
                                    </a>
                                <#else>
                                    <span class="jive-pagination-prev-none"><@s.text name="global.previous"/></span>
                                </#if>
                                <#if (paginator.nextPage)>
                                    <a href="#/pages/${paginator.getPageIndex() + 2}" class="jive-pagination-next">
                                        <@s.text name="global.next"/>
                                    </a>
                                <#else>
                                    <span class="jive-pagination-next-none"><@s.text name="global.next"/></span>
                                </#if>
                            </span>
                        </span>
                        <!-- END pagination -->
                    </#if>
                </div>
                <!-- END table footer -->
                </#if>
            </div>
        </div>
        <!-- BEGIN content results -->
    </div>
</div>
<!-- END content list -->