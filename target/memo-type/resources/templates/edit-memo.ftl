<#include "/template/global/include/tag-macros.ftl" />
<#include "/template/global/include/jive-form-elements.ftl" />

<#assign isSpellCheckEnabled = JiveGlobals.getJiveBooleanProperty("skin.default.spellCheckEnabled", true)>
<#assign isWikiTablesEnabled = WikiUtils.isWikiTableSyntaxEnabled(container)>
<#assign isWikiImagesEnabled = WikiUtils.isWikiImageSyntaxEnabled(container) && action.hasPermissionsToUploadImages()>
<html>
    <head>
        <title><#if (edit)><@s.text name="memo.edit.title" /><#else><@s.text name="memo.create.title" /></#if></title>
        <style type="text/css" media="screen">
            @import "<@s.url value='/styles/jive-compose.css'/>";
            @import "<@s.url value='/styles/jive-wiki.css'/>";
        </style>
        
        <meta name="nosidebar" content="true"/>
        <content tag="breadcrumb">
            <#if container?exists>
                <@s.action name="community-breadcrumb" executeResult="true" ignoreContextParams="true">
                    <@s.param name="container" value="${container.ID?c}"/>
                    <@s.param name="containerType" value="${container.objectType?c}"/>
                </@s.action>
            </#if>
        </content>
    
        <@resource.javascript>
        $j(function() {
                $j("#jive-compose-categories input").change(function() {
                    if ( $j(this).closest('span').hasClass('jive-category-highlight') ) {
                        $j(this).closest('span').removeClass('jive-category-highlight');
                        $j(this).closest('span').find('img').remove();
                    }
                    return false;
                });
            });
    
    
            function highlightCategory(theTag, theCategory) {
                var tag = ("#" + theTag);
                var category = ("#" + theCategory);
                if ( $j(category).is(":not(:checked)") && ($j(category).closest('span').hasClass('jive-category-highlight') == false) ) {
                    $j(category).closest('span').toggleClass('jive-category-highlight', 300, function() {
                        $j(category).closest('span').find('label').append("<img id='tags-tooltip' class='jive-icon-med jive-icon-help jiveTT-hover-suggest' alt='' src='images/transparent.png'/>");
                        $j('#jive-tag2').text($j(theTag).text());
                        $j('#jive-cat2').text($j(category).closest('span').find('label').text());
                    });
                }
                return false;
            }
        </@resource.javascript>
    
    		    <#-- used for simple editor -->
	    <@resource.javascript>
            var _editor_lang = "${locale.language}";
            var _jive_is_reply = false;
            var _jive_spell_check_enabled = "${isSpellCheckEnabled?string}";
            var _jive_gui_quote_text = "";
            var _jive_video_picker__url = "?container=${container.ID?c}&containerType=${container.objectType?c}"
            <#if (edit)>
                var _jive_image_picker_url = "/edit-memo!imagePicker.jspa?memo=${(memo.ID?c)!-1}&postedFromGUIEditor=true";
            <#else>
                var _jive_image_picker_url = "/create-memo!imagePicker.jspa?memo=${(memo.ID?c)!-1}&containerType=${container.objectType?c}&container=${container.ID?c}&postedFromGUIEditor=true";
            </#if>
            var _jive_tables_enabled = "${isWikiTablesEnabled?string}";
            var _jive_images_enabled = "${isWikiImagesEnabled?string}";
	    </@resource.javascript>
	    <@resource.javascript file="/resources/scripts/post.js" header="true" />
	
	    <#-- Include JavaScript Library and RTE -->
	    <#include "/template/decorator/default/header-javascript-jive-core.ftl" />
	    <@resource.javascript header="true">
	    ${macroJavaScript}
	    </@resource.javascript>
	    <#include "/template/decorator/default/header-javascript-minirte.ftl" />
	    <#include "/template/decorator/default/header-javascript-rte.ftl" />
	    <@resource.dwr file="WikiTextConverter" />
   
    </head>
    <body class="jive-body-formpage jive-body-formpage-memo">
    
        <!-- BEGIN header & intro  -->
        <div id="jive-body-intro">
            <div id="jive-body-intro-content">
                <h1><#if (edit)><@s.text name="memo.edit.title" /><#else><@s.text name="memo.create.title" /></#if></h1>
            </div>
        </div>
        <!-- END header & intro -->
    
        <!-- BEGIN main body -->
        <div id="jive-body-main">   
            <!-- BEGIN main body column -->
            <div id="jive-body-maincol-container">
                <div id="jive-body-maincol">
                    <#include "/template/global/include/form-message.ftl" />
                    <div class="jive-standard-formblock-container">
                        <div class="jive-standard-formblock">
                            <form action=<#if (edit)>"edit-memo.jspa"<#else>"create-memo.jspa"</#if> method="post" name="memoform" onSubmit="validateMessage()">
                                <#if memo?exists>
                                <input type="hidden" name="memo" value="${memo.ID?c}"/>
                                </#if>
                                <#if container?exists>
                                <input type="hidden" name="container" value="${container.ID?c}"/>
                                <input type="hidden" name="containerType" value="${container.objectType?c}"/>
                                </#if>
                                <div class="jive-form">
                                    <div class="form-row">
                                        <div class="jive-form-label">
                                            <label class="jive-formfield-required" for="title">
                                                <@s.text name="memo.edit.title.label" /> <em class="required"><@s.text name="global.left_paren" /><@s.text name="global.required"/><@s.text name="global.right_paren" /></em>
                                            </label>
                                        </div>
                                        <div class="jive-form-element">
                                            <input id="title" class="jive-form-textinput" type="text" size="50" name="title" value="${title!?html}" />
                                            <@macroFieldErrors name="title"/>
                                        </div>
                                    </div>
                                    <div class="form-row">
                                        <div class="jive-form-label">
                                            <label class="jive-formfield-required" for="description">
                                                <@s.text name="memo.edit.description.label" /> <em class="required"><@s.text name="global.left_paren" /><@s.text name="global.required"/><@s.text name="global.right_paren" /></em>
                                            </label>
                                        </div>
                                        <div class="jive-form-element">
                                   			<div id="jive-memo-bodybox">
	                 							<div class="jive-editor-panel">
	                                        		<div class='wysiwygtext_html_link'>
	                                            		<a href='javascript:;' class='toggle_html' id='toggle_html' style='display:none;'><@s.text name="rte.toggle_display" /></a>
	                                            		<a href="javascript:void(0);" onmousedown="setPreferredEditorMode(getEditorMode());return false;"
	                                             			class="jive-description jivePreferredEditorModeLinkHREF" style="display:none;" id="jivePreferredEditorModeLinkHREF"
	                                                    	><@s.text name="post.alwaysUseThisEditor.tab" /></a>
	                                        		</div>
	                                        		<div class="jive-panel-wrapper" id="editor-panel-wrapper">
	                                            		<div id="wysiwyg-panel" class="current">
	                                                		<textarea id='wysiwygtext' >${description!?html}</textarea>
	                                                		<textarea style="padding:0em;display:none;" name="description" id="textEditor" rows="5" cols="30">${description!?html}</textarea>
	                                            		</div>
	                                        		</div>
	                                    		</div>
											</div>
                                        
                                            <@macroFieldErrors name="description"/>
                                        </div>
                                    </div>
                                
                                    <!-- BEGIN compose section -->
                                    <div class="jive-compose-section jive-compose-section-cats-tags">
                        
                                        <#if !action.isUserContainer(container)>
                                            <#assign objectTagSetIDs = action.getObjectTagSetIDs(memo)>
                                            <#include "/template/global/include/category.ftl" />
                                        </#if>
                                    
                                        <@macroFieldErrors name="tags"/>
                        
                                        <div id="jive-compose-tags">
                                            <span id="jive-compose-tags-container">
                        
                                                <h4><span class="jive-icon-med jive-icon-tag"></span>
                                                    <@s.text name="memo.tags.title" />
                                                    <span id="tag_directions" class="jive-compose-directions font-color-meta-light"><@s.text name="memo.spaceSeprtsTags.text" /></span>
                                                </h4>
                        
                                                <div id="jive-compose-tags-form">
                        
                                                    <input type="text" name="tags" size="65" id="jive-tags"
                                                        onKeyUp="processKey(this);"
                                                        value="${tags!?html}" />
                        
                                                    <div id="jive-tag-choices-container"><div id="jive-tag-choices" class="autocomplete"></div></div>
                        
                                                    <#if (popularTags?size > 0)>
                                                        <div id="jive-populartags-container">
                                                            <span>
                                                                <strong><@s.text name="memo.create.popular_tags.gtitle" /><@s.text name="global.colon" /></strong>
                                                                <#if container.objectType == JiveConstants.SOCIAL_GROUP>
                                                                    <@s.text name="memo.tags.group.popular.instructions" />
                                                                <#elseif container.objectType == JiveConstants.PROJECT>
                                                                    <@s.text name="memo.tags.project.popular.instructions" />
                                                                <#else>
                                                                    <@s.text name="memo.tags.popular.instructions" />
                                                                </#if>
                                                            </span>
                                                            <div>
                                                                <#list popularTags as tag>
                                                                    <a name="populartag" rel="nofollow" href="#" onclick="swapTag(this); <#if !action.isUserContainer(container)>TagSet.highlightCategory('${tag?js_string}');</#if> return false;"
                                                                    <#if (tags?exists && ((tags.indexOf(' ' + tag + ' ') > -1) || (tags.startsWith(tag + ' ')) || (tags.endsWith(' ' + tag))))>
                                                                        class="jive-tagname-${tag?html} jive-tag-selected"
                                                                    <#else>
                                                                        class="jive-tagname-${tag?html} jive-tag-unselected"
                                                                    </#if>
                                                                    >${action.renderTagToHtml(tag)}</a>&nbsp;
                                                                </#list>
                                                            </div>
                                                        </div>
                                                    </#if>
                                                </div>
                        
                                                <!-- NOTE: this include MUST come after the 'tags' input element -->
                                                <script language="JavaScript" type='text/javascript' src="<@s.url value='/resources/scripts/tag-selector.js' />"></script>
                                            </span>
                                        </div>
                                    </div>
                                    <!-- END compose section -->
                                    <div class="form-row">
                                        <div class="jive-form-label"></div>
                                        <div class="jive-form-element">
                                            <input type="submit" value="<#if (edit)><@s.text name="memo.edit.update.button.text" /><#else><@s.text name="memo.edit.create.button.text" /></#if>" name="save" />
                                            <input type="submit" value="Cancel" name="method:cancel" />
                                        </div>
                                    </div>                                  
                                </div>
                            </form>
                        </div>
                    </div>  
                </div>
            </div>
            <!-- END main body column -->
        </div>
		<@resource.javascript>
	        function validateMessage(){
	            var body = window.editor.get('wysiwygtext').getHTML();
	            $('textEditor').value = body;
	            // safari 1.x and 2.x bug: http://lists.apple.com/archives/Web-dev/2005/Feb/msg00106.html
	            if(window.editor.get('wysiwygtext').isTextOnly()){
	                $('textEditor').style.display = "inline";
	                $('textEditor').innerHTML = "";
	                $('textEditor').appendChild(document.createTextNode(body));
	                $('textEditor').style.display = "none";
	            }
	            return true;
	        }

	        function setPreferredEditorMode(mode) {
	            WikiTextConverter.setPreferredEditorMode(mode,
	                {
	                    callback: function() {
	                        preferredMode = mode;
	                        refreshLinks();
	                    },
	                    timeout: DWRTimeout // 20 seconds
	                }
	            );
	        }
	
	        function getEditorMode() {
	            return currentMode;
	        }

	        function initEditor() {
	
	            Event.observe($('toggle_html'), "click", function() {
	                window.editor.get('wysiwygtext').toggleEditorMode('wysiwygtext');
	            });
	
	            var h = 200;
	            window.editor.get('wysiwygtext').resizeTo(h);
	            if(tinymce.isIE){
	                // IE fails at rendering the mini rte in comments
	                // unless i toggle it twice. super awesome.
	                window.editor.get('wysiwygtext').toggleEditorMode();
	                window.editor.get('wysiwygtext').toggleEditorMode();
	            }
	            if(currentMode == "rawhtml"){
	                window.editor.get('wysiwygtext').toggleEditorMode();
	            }
	
	            var list = new jive.rte.RTEListener();
	            list.doneTogglingMode = function(){
	                if(currentMode == "advanced"){
	                    currentMode = "rawhtml";
	                }else{
	                    currentMode = "advanced";
	                }
	                refreshLinks();
	            }
	            window.editor.get('wysiwygtext').addListener(list);
	
	        }
	
	        function refreshLinks(){
	            if(currentMode == "rawhtml"){
	                $('toggle_html').style.display = "block";
	            }else{
	                $('toggle_html').style.display = "none";
	            }
	            if(preferredMode == currentMode){
	                $('jivePreferredEditorModeLinkHREF').style.display = "none";
	            }else{
	                $('jivePreferredEditorModeLinkHREF').style.display = "block";
	            }
	        }

	        function initRTE(){
	            if(!window.editor.get('wysiwygtext').isReady()){
	                window.setTimeout("initRTE()", 33);
	                return;
	            }
	
	            if(window.editor.get('wysiwygtext').isTextOnly()){
	                $('wysiwyg-panel').removeClassName('loading');
	                preferredMode = 'rawhtml';
	                currentMode = 'rawhtml';
	                return;
	            }else{
	                preferredMode = '${preferredEditorMode}';
	                currentMode = '${preferredEditorMode}';
	            }
	
	            //
	            // set the editor's mode to the user's preference
	            // on page startup
	            document.getElementById('wysiwyg-panel').style.display = 'block';
	            refreshLinks();
	
	            initEditor();
	        }
	
	        function buildRTE(){
	            window.editor.put('wysiwygtext', new jive.rte.RTE(jiveControl, "wysiwygtext", "mini"));
	            initRTE();
	        }
	
	        /**
	         * new initialize function for the editor
	         */
	        window.editor = new jive.ext.y.HashTable();
	        jive.ext.x.xAddEventListener(window, "load", buildRTE);
	        window.DWRTimeout = 20000;

		</@resource.javascript>
         <content tag="jiveTooltip">
            <div id="jiveTT-note-tags" class="jive-tooltip-help notedefault snp-mouseoffset" >
                <@s.text name="doc.create.tag_explained.info" />
            </div>
            
            <div id="jiveTT-note-suggest" class="jive-tooltip2 notedefault snp-mouseoffset"  >
                <div class="jive-tooltip2-top"></div>
                <div class="jive-tooltip2-mid">
                    <div class="jive-tooltip2-mid-padding" id="jive-note-category-suggestion-body">
                        <strong>Suggested Category</strong>
                    </div>
            
                </div>
                <div class="jive-tooltip2-btm"></div>
            </div>
        </content>
    </body>
</html>
