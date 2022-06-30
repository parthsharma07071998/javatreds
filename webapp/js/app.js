var left_side_width = 220; //Sidebar width in pixels

var public_vars = public_vars || {};
public_vars.$body                 = $("body");
public_vars.$pageLoadingOverlay   = public_vars.$body.find('.page-loading-overlay');

$(function() {
    "use strict";

    //Enable sidebar toggle
    $("[data-toggle='offcanvas']").click(function(e) {
        e.preventDefault();

        //If window is small enough, enable sidebar push menu
        if ($(window).width() <= 992) {
            $('.row-offcanvas').toggleClass('active');
            $('.left-side').removeClass("collapse-left");
            $(".right-side").removeClass("strech");
            $('.row-offcanvas').toggleClass("relative");
        } else {
            //Else, enable content streching
            $('.left-side').toggleClass("collapse-left");
            $(".right-side").toggleClass("strech");
        }
        sessionStorage.leftMenuHidden = $('.left-side').hasClass("collapse-left")?'Y':'N';
    });
    if (sessionStorage.leftMenuHidden=='Y') {
    	$("[data-toggle='offcanvas']").trigger('click');
    }
    //Add hover support for touch devices
    $('.btn').bind('touchstart', function() {
        $(this).addClass('hover');
    }).bind('touchend', function() {
        $(this).removeClass('hover');
    });

    //Activate tooltips
    $("[data-toggle='tooltip']").tooltip();

    /*     
     * Add collapse and remove events to boxes
     */
    $("[data-widget='collapse']").click(function() {
        //Find the box parent        
        var box = $(this).parents(".box").first();
        //Find the body and the footer
        var bf = box.find(".box-body, .box-footer");
        if (!box.hasClass("collapsed-box")) {
            box.addClass("collapsed-box");
            bf.slideUp();
        } else {
            box.removeClass("collapsed-box");
            bf.slideDown();
        }
    });

    /*
     * ADD SLIMSCROLL TO THE TOP NAV DROPDOWNS
     * ---------------------------------------
     */
    $(".slim-scroll").slimscroll({
        alwaysVisible: false,
        size: "5px"
    });

    /*
     * INITIALIZE BUTTON TOGGLE
     * ------------------------
     */
    $('.btn-group[data-toggle="btn-toggle"]').each(function() {
        var group = $(this);
        $(this).find(".btn").click(function(e) {
            group.find(".btn.active").removeClass("active");
            $(this).addClass("active");
            e.preventDefault();
        });

    });

    $("[data-widget='remove']").click(function() {
        //Find the box parent        
        var box = $(this).parents(".box").first();
        box.slideUp();
    });
    //console.log("I was called : 1");
    /* Sidebar tree view */
    //$(".sidebar .treeview").tree();

    /* 
     * Make sure that the sidebar is streched full height
     * ---------------------------------------------
     * We are gonna assign a min-height value every time the
     * wrapper gets resized and upon page load. We will use
     * Ben Alman's method for detecting the resize event.
     * 
     **/
    function _fix() {
        $(".wrapper").css("min-height", ($(window).height()-150) + "px");
        $(".left-side, html, body").css("min-height", $(window).height() + "px");
        //Get window height and the wrapper height
        /*var height = $(window).height() - $("body > .header").height();
        console.log(":>>"+$(window).height()+":"+$("body > .header").length);
        var content = $(".wrapper").height();
        console.log(height+":"+content+":"+(content>height?content:height));
        $(".left-side, html, body").css("min-height", (content>height?content:height) + "px");*/
    }
    //Fire upon load
    _fix();
    //Fire when wrapper is resized
    $(".wrapper").resize(function() {
        _fix();
        fix_sidebar();
    });

    //Fix the fixed layout sidebar scroll bug
    fix_sidebar();

});
function fix_sidebar() {
    //Make sure the body tag has the .fixed class
    if (!$("body").hasClass("fixed")) {
        return;
    }

    //Add slimscroll
	var lHeight = ($(window).height() - $(".header").height());
    $(".sidebar").slimscroll({
        height: lHeight + "px",
        color: "rgba(0,0,0,0.2)"
    });
}
function change_layout() {
    $("body").toggleClass("fixed");
    fix_sidebar();
}
function change_skin(cls) {
    $("body").removeClass("skin-blue skin-black");
    $("body").addClass(cls);
}
(function($) {
    "use strict";

    $.fn.tree = function() {

        return this.each(function() {
            var btn = $(this).children("a").first();
            var menu = $(this).children(".treeview-menu").first();
            var isActive = $(this).hasClass('active');

            //initialize already active menus
            if (isActive) {
                menu.show();
                btn.children(".fa-angle-left").first().removeClass("fa-angle-left").addClass("fa-angle-down");
            }
            //Slide open or close the menu on link click
            btn.click(function(e) {
                e.preventDefault();
                if (isActive) {
                    //Slide up to close menu
                    menu.slideUp();
                    isActive = false;
                    btn.children(".fa-angle-down").first().removeClass("fa-angle-down").addClass("fa-angle-left");
                    btn.parent("li").removeClass("active");
                } else {
                    //Slide down to open menu
                    menu.slideDown();
                    isActive = true;
                    btn.children(".fa-angle-left").first().removeClass("fa-angle-left").addClass("fa-angle-down");
                    btn.parent("li").addClass("active");
                }
            });

            /* Add margins to submenu elements to give it a tree look */
            menu.find("li > a").each(function() {
                var pad = parseInt($(this).css("margin-left")) + 10;

                $(this).css({"margin-left": pad + "px"});
            });

        });

    };


}(jQuery));

/* CENTER ELEMENTS */
(function($) {
    "use strict";
    jQuery.fn.center = function(parent) {
        if (parent) {
            parent = this.parent();
        } else {
            parent = window;
        }
        this.css({
            "position": "absolute",
            "top": ((($(parent).height() - this.outerHeight()) / 2) + $(parent).scrollTop() + "px"),
            "left": ((($(parent).width() - this.outerWidth()) / 2) + $(parent).scrollLeft() + "px")
        });
        return this;
    }
}(jQuery));

/*
 * jQuery resize event - v1.1 - 3/14/2010
 * http://benalman.com/projects/jquery-resize-plugin/
 * 
 * Copyright (c) 2010 "Cowboy" Ben Alman
 * Dual licensed under the MIT and GPL licenses.
 * http://benalman.com/about/license/
 */
(function($, h, c) {
    var a = $([]), e = $.resize = $.extend($.resize, {}), i, k = "setTimeout", j = "resize", d = j + "-special-event", b = "delay", f = "throttleWindow";
    e[b] = 250;
    e[f] = true;
    $.event.special[j] = {setup: function() {
            if (!e[f] && this[k]) {
                return false;
            }
            var l = $(this);
            a = a.add(l);
            $.data(this, d, {w: l.width(), h: l.height()});
            if (a.length === 1) {
                g();
            }
        }, teardown: function() {
            if (!e[f] && this[k]) {
                return false
            }
            var l = $(this);
            a = a.not(l);
            l.removeData(d);
            if (!a.length) {
                clearTimeout(i);
            }
        }, add: function(l) {
            if (!e[f] && this[k]) {
                return false
            }
            var n;
            function m(s, o, p) {
                var q = $(this), r = $.data(this, d);
                if (r) {
                    r.w = o !== c ? o : q.width();
                    r.h = p !== c ? p : q.height();
                }
                n.apply(this, arguments)
            }
            if ($.isFunction(l)) {
                n = l;
                return m
            } else {
                n = l.handler;
                l.handler = m
            }
        }};
    function g() {
        i = h[k](function() {
            a.each(function() {
                var n = $(this), m = n.width(), l = n.height(), o = $.data(this, d);
                if (m !== o.w || l !== o.h) {
                    n.trigger(j, [o.w = m, o.h = l])
                }
            });
            g()
        }, e[b])
    }}
)(jQuery, this);

/*!
 * SlimScroll https://github.com/rochal/jQuery-slimScroll
 * =======================================================
 * 
 * Copyright (c) 2011 Piotr Rochala (http://rocha.la) Dual licensed under the MIT 
 */
(function(f) {
    jQuery.fn.extend({slimScroll: function(h) {
            var a = f.extend({width: "auto", height: "250px", size: "7px", color: "#000", position: "right", distance: "1px", start: "top", opacity: 0.4, alwaysVisible: !1, disableFadeOut: !1, railVisible: !1, railColor: "#333", railOpacity: 0.2, railDraggable: !0, railClass: "slimScrollRail", barClass: "slimScrollBar", wrapperClass: "slimScrollDiv", allowPageScroll: !1, wheelStep: 20, touchScrollStep: 200, borderRadius: "0px", railBorderRadius: "0px"}, h);
            this.each(function() {
                function r(d) {
                    if (s) {
                        d = d ||
                                window.event;
                        var c = 0;
                        d.wheelDelta && (c = -d.wheelDelta / 120);
                        d.detail && (c = d.detail / 3);
                        f(d.target || d.srcTarget || d.srcElement).closest("." + a.wrapperClass).is(b.parent()) && m(c, !0);
                        d.preventDefault && !k && d.preventDefault();
                        k || (d.returnValue = !1)
                    }
                }
                function m(d, f, h) {
                    k = !1;
                    var e = d, g = b.outerHeight() - c.outerHeight();
                    f && (e = parseInt(c.css("top")) + d * parseInt(a.wheelStep) / 100 * c.outerHeight(), e = Math.min(Math.max(e, 0), g), e = 0 < d ? Math.ceil(e) : Math.floor(e), c.css({top: e + "px"}));
                    l = parseInt(c.css("top")) / (b.outerHeight() - c.outerHeight());
                    e = l * (b[0].scrollHeight - b.outerHeight());
                    h && (e = d, d = e / b[0].scrollHeight * b.outerHeight(), d = Math.min(Math.max(d, 0), g), c.css({top: d + "px"}));
                    b.scrollTop(e);
                    b.trigger("slimscrolling", ~~e);
                    v();
                    p()
                }
                function C() {
                    window.addEventListener ? (this.addEventListener("DOMMouseScroll", r, !1), this.addEventListener("mousewheel", r, !1), this.addEventListener("MozMousePixelScroll", r, !1)) : document.attachEvent("onmousewheel", r)
                }
                function w() {
                    u = Math.max(b.outerHeight() / b[0].scrollHeight * b.outerHeight(), D);
                    c.css({height: u + "px"});
                    var a = u == b.outerHeight() ? "none" : "block";
                    c.css({display: a})
                }
                function v() {
                    w();
                    clearTimeout(A);
                    l == ~~l ? (k = a.allowPageScroll, B != l && b.trigger("slimscroll", 0 == ~~l ? "top" : "bottom")) : k = !1;
                    B = l;
                    u >= b.outerHeight() ? k = !0 : (c.stop(!0, !0).fadeIn("fast"), a.railVisible && g.stop(!0, !0).fadeIn("fast"))
                }
                function p() {
                    a.alwaysVisible || (A = setTimeout(function() {
                        a.disableFadeOut && s || (x || y) || (c.fadeOut("slow"), g.fadeOut("slow"))
                    }, 1E3))
                }
                var s, x, y, A, z, u, l, B, D = 30, k = !1, b = f(this);
                if (b.parent().hasClass(a.wrapperClass)) {
                    var n = b.scrollTop(),
                            c = b.parent().find("." + a.barClass), g = b.parent().find("." + a.railClass);
                    w();
                    if (f.isPlainObject(h)) {
                        if ("height"in h && "auto" == h.height) {
                            b.parent().css("height", "auto");
                            b.css("height", "auto");
                            var q = b.parent().parent().height();
                            b.parent().css("height", q);
                            b.css("height", q)
                        }
                        if ("scrollTo"in h)
                            n = parseInt(a.scrollTo);
                        else if ("scrollBy"in h)
                            n += parseInt(a.scrollBy);
                        else if ("destroy"in h) {
                            c.remove();
                            g.remove();
                            b.unwrap();
                            return
                        }
                        m(n, !1, !0)
                    }
                } else {
                    a.height = "auto" == a.height ? b.parent().height() : a.height;
                    n = f("<div></div>").addClass(a.wrapperClass).css({position: "relative",
                        overflow: "hidden", width: a.width, height: a.height});
                    b.css({overflow: "hidden", width: a.width, height: a.height});
                    var g = f("<div></div>").addClass(a.railClass).css({width: a.size, height: "100%", position: "absolute", top: 0, display: a.alwaysVisible && a.railVisible ? "block" : "none", "border-radius": a.railBorderRadius, background: a.railColor, opacity: a.railOpacity, zIndex: 90}), c = f("<div></div>").addClass(a.barClass).css({background: a.color, width: a.size, position: "absolute", top: 0, opacity: a.opacity, display: a.alwaysVisible ?
                                "block" : "none", "border-radius": a.borderRadius, BorderRadius: a.borderRadius, MozBorderRadius: a.borderRadius, WebkitBorderRadius: a.borderRadius, zIndex: 99}), q = "right" == a.position ? {right: a.distance} : {left: a.distance};
                    g.css(q);
                    c.css(q);
                    b.wrap(n);
                    b.parent().append(c);
                    b.parent().append(g);
                    a.railDraggable && c.bind("mousedown", function(a) {
                        var b = f(document);
                        y = !0;
                        t = parseFloat(c.css("top"));
                        pageY = a.pageY;
                        b.bind("mousemove.slimscroll", function(a) {
                            currTop = t + a.pageY - pageY;
                            c.css("top", currTop);
                            m(0, c.position().top, !1)
                        });
                        b.bind("mouseup.slimscroll", function(a) {
                            y = !1;
                            p();
                            b.unbind(".slimscroll")
                        });
                        return!1
                    }).bind("selectstart.slimscroll", function(a) {
                        a.stopPropagation();
                        a.preventDefault();
                        return!1
                    });
                    g.hover(function() {
                        v()
                    }, function() {
                        p()
                    });
                    c.hover(function() {
                        x = !0
                    }, function() {
                        x = !1
                    });
                    b.hover(function() {
                        s = !0;
                        v();
                        p()
                    }, function() {
                        s = !1;
                        p()
                    });
                    b.bind("touchstart", function(a, b) {
                        a.originalEvent.touches.length && (z = a.originalEvent.touches[0].pageY)
                    });
                    b.bind("touchmove", function(b) {
                        k || b.originalEvent.preventDefault();
                        b.originalEvent.touches.length &&
                                (m((z - b.originalEvent.touches[0].pageY) / a.touchScrollStep, !0), z = b.originalEvent.touches[0].pageY)
                    });
                    w();
                    "bottom" === a.start ? (c.css({top: b.outerHeight() - c.outerHeight()}), m(0, !0)) : "top" !== a.start && (m(f(a.start).position().top, null, !0), a.alwaysVisible || c.hide());
                    C()
                }
            });
            return this
        }});
    jQuery.fn.extend({slimscroll: jQuery.fn.slimScroll})
})(jQuery);

/**********/

var loginData = {};
var regMenu;
var dateFormatter, timeFormatter, dateTimeFormatter;
var tplNotifications;
var divAlert$;
var ISTOFFSET = 19800000,ALERTDURATION=5000;
var remoteContent={};
var modalRemote$;
var loading$;

$(document).ready(function() {
	updateLoginState();
	/** Notifications **/
	var lTemplate = $('#tplNotifications').html();
	if (lTemplate)
		tplNotifications=Handlebars.compile(lTemplate);
	$('.notifications-menu').on('show.bs.dropdown', showNotifications);
	divAlert$=$('.alert-bottom');
	divAlert$.children(' .close').on('click', hideAlert);
	var lNotifications=getNotifications();
	showNewCount(lNotifications.newCount);
	/** Notifications **/
	modalRemote$ = $('#mdl-remote');
	modalRemote$.on('hidden.bs.modal', function (e) {
		modalRemote$.find('.modal-body').empty();
		modalRemote$.find('.modal-header span').html("&nbsp;");
	})
	loading$=$('#img-loading');
	$(document).ajaxStart(function(){
		loading$.removeClass('hidden');
	});
	$(document).ajaxStop(function(){
		loading$.addClass('hidden');
	});
	var lMsg=sessionStorage.getItem("pageMsg");
	if (lMsg != null) {
		sessionStorage.removeItem("pageMsg");
		showAlert(INFO, lMsg);
	}
	FORMAT_DATE="dd-MMM-yyyy";
	FORMAT_DATETIME = FORMAT_DATE + " " + FORMAT_TIME;
	dateFormatter = new SimpleDateFormat(FORMAT_DATE);
	timeFormatter = new SimpleDateFormat(FORMAT_TIME);
	dateTimeFormatter = new SimpleDateFormat(FORMAT_DATETIME);
	updateDateTime();
	setInterval(updateDateTime, 1000);
	if(public_vars.$pageLoadingOverlay.length)
	{
			public_vars.$pageLoadingOverlay.addClass('loaded');
	}
});
function updateDateTime() {
	var lDate=new Date();
	var lMillis=lDate.getTime();
	if (loginData && (loginData.timeOffset!=null))
		lMillis+=loginData.timeOffset;
	var lCurDate=getDisplayDate(lMillis);
	var lCurTime=getDisplayTime(lMillis);
	$('#spnDate').html(lCurDate);
	$('#spnTime').html(lCurTime);
	if (loginData) {
		loginData.curDate=lCurDate;
		loginData.curTime=lCurTime;
	}
	if (window.handleClock)
		window.handleClock(lMillis);
}
function updateLoginState() {
	loginData = sessionStorage.sessionDetails?JSON.parse(sessionStorage.sessionDetails):null;
	if (loginData && loginData.login) {
		constructMenu();
		var lFullName = loginData.firstName;
		if (loginData.lastName) lFullName += " " + loginData.lastName;
		var lName = lFullName;
		if (lName.length>15) lName=loginData.firstName;
		$('#spnUser').html(htmlEscape(lName));
		if(loginData.domain=='REGUSER'){
			$('.spnCompany').html(htmlEscape(lFullName));			
		}else{
			$('.spnCompany').html(htmlEscape(loginData.entity+' ('+loginData.domain+')'));
		}
		$('#spnUserFull').html(htmlEscape(lFullName));
		$('#spnEntityTypeDesc').html(loginData.entityTypeDesc);
		$('#spnLoginId').html(htmlEscape(loginData.login));
		timeOffset = loginData.serverTime-(new Date()).getTime();
		//setImage($('#imgLogo'),'upload/ENTITYLOGOS/',loginData.logo);
		$(".state-logout").hide();
		$(".state-login").show();
		if (loginData.userType==1)
			$(".state-admin").show();
		else
			$(".state-admin").hide();
		refreshState();
	} else {
		if (!window.publicPage) {
			location.href='login';
			return;
		}
		$(".state-login").hide();
		$(".state-logout").show();
		$("[data-toggle='offcanvas']").trigger('click');
	}
	$.ajaxSetup({
		dataType: "json",
	    contentType: "application/json",
		timeout: 120000,
		cache: false,
		headers: {
	        "loginKey": loginData==null?null:loginData.loginKey
	    },
	    statusCode: {
	    	401: function(pXhr) {
	    		var lMsg = null;
	    		try {
	    			var lErrObj = JSON.parse(pXhr.responseText);
	    			lMsg = lErrObj.messages[0];
	    		} catch (e) {
	    		}
	    		if (!lMsg) lMsg = "Some error occurred : " + pXhr.responseText;
	    		alert(lMsg, null, function() {
	    			location.href='login';
	    		});
	    	}
	    }
	});
	// datatables defaultTableConfig
	window.defaultXcrudConfig = {
		"filterStateSave":false	
	};
	window.defaultTableConfig = {
	
	};
	
}
function refreshState() {
	if (loginData && loginData.entityTypeList) {
		$.each(loginData.entityTypeList,function(pIndex, pValue){
			$(".state-" + pValue).show();
		});
	}
}
function constructMenu() {
	var lMenu = regMenu?regMenu:loginData.menu;
	if($("#side-menu-template").length) {
		// The main template.
		var lMenuTmpl = Handlebars.compile($("#side-menu-template").html());
		// Register the list partial that "main" uses.
		Handlebars.registerPartial("menulist-template", $("#menulist-template").html());
		// Render the list.
		$("#ul-sidebar-menu").empty();
		$("#ul-sidebar-menu").html(lMenuTmpl(lMenu));
		var lCurHrefParts = window.location.href.split('/');
		var lCurHref = lCurHrefParts[lCurHrefParts.length-1];
		var lCurHref1 = lCurHref.split('?')[0];
		var lMenu$,lMenu1$;
		$('#ul-sidebar-menu li a').each(function(pIndex){
			var lHref = $(this).attr('href');
			if (lCurHref.substr(0,lHref.length)===lHref) lMenu$=$(this).parents('li');
			else if (lCurHref1===lHref) lMenu1$=$(this).parents('li');
		});
		if (lMenu$) lMenu$.addClass('active');
		else if (lMenu1$) lMenu1$.addClass('active');
		$(".sidebar .treeview").tree();
	}
	// access control
	$(".secure").each(function(pIndex) {
		var lThis$=$(this);
		var lSecKey=lThis$.data('seckey');
		if (loginData.secKeys[lSecKey]!=null)
			lThis$.show();
	});
}
function postLogin(pObj, pNewLogin) {
	sessionStorage.clear();
	pObj.timeOffset = pObj.serverTime-(new Date()).getTime();
	sessionStorage.sessionDetails = JSON.stringify(pObj);
	if(pObj.menulist != null)
		sessionStorage.setItem("menulist",JSON.stringify(pObj.menulist))
	updateLoginState();
	if (pNewLogin) {
		var lMsg = "Welcome to TREDS.";
		if (pObj.lastLogin)
			lMsg += " Your last login was on " + getDisplayDateTime(pObj.lastLogin);
		//addNotification(INFO, lMsg);
		sessionStorage.setItem("pageMsg",lMsg);
	}
}
function logout(pUrl) {
	sessionStorage.clear();
	showNewCount(0);
	$.ajax( {
        url: "logout",
        type: "GET",
        success: function( pObj, pStatus, pXhr) {
        	//updateLoginState();
        	//showAlert(INFO, "Logged out successfully", ALERTDURATION);
        	location.href=pUrl?pUrl:'login';
        },
        error: errorHandler
	});
}
function showIpWhiteList() {
	location.href='appentity?code='+loginData.domain;
}
function showRemote(pUrl, pClass, pNoCache, pTitle) {
	var lContent=remoteContent[pUrl];
	if (!lContent) {
		$.ajax({
			url: pUrl,
			type: "GET",
			dataType: "html",
			success: function( pObj, pStatus, pXhr) {
				remoteContent[pUrl]=pObj;
				showRemote(pUrl, pClass, pNoCache, pTitle);
			},
			error: errorHandler
		});
	} else {
		if (pNoCache)
			delete remoteContent[pUrl];
		modalRemote$.find('.modal-body').html(lContent);
		var lBody$ = modalRemote$.find('.modal-dialog');
		lBody$.removeClass();
		lBody$.addClass('modal-dialog');
		if (pClass)
			lBody$.addClass(pClass);
		modalRemote$.find('.modal-header span').html(pTitle==null?'&nbsp;':pTitle);
		modalRemote$.modal('show',{keyboard:true});
	}
}
function closeRemote() {
	modalRemote$.modal('hide');
}
/**** Notifications ****/
function getNotifications() {
	var lNotifications=sessionStorage.notifications?JSON.parse(sessionStorage.notifications):{};
	if (!lNotifications.list) {
		lNotifications.newCount = 0;
		lNotifications.list = [];
	}
	return lNotifications;
}
function saveNotifications(pNotifications) {
	sessionStorage.notifications = JSON.stringify(pNotifications);
}

function addNotification(pType, pMessage, pTime) {
	if (pTime==null) pTime=new Date();
	showAlert(pType,pMessage,ALERTDURATION);
	var lNotifications=getNotifications();
	lNotifications.newCount+=1;
	showNewCount(lNotifications.newCount);
	lNotifications.list.unshift({"type":pType,"message":pMessage,"time":getDisplayTime(pTime+ISTOFFSET),"new":true});
	lNotifications.total=lNotifications.list.length;
	saveNotifications(lNotifications);
}
function showNotifications() {
	var lNotifications=getNotifications();
	if ($('.notifications-menu span.label')!="") {
		var lMenu$ = $('.notifications-menu .dropdown-menu');
		lMenu$.html(tplNotifications(lNotifications));
		lMenu$.find('ul').slimscroll({
			height: "200px",
	        alwaysVisible: false,
	        size: "5px"
	    });
	}
	lNotifications.newCount=0;
	showNewCount(lNotifications.newCount);
	$.each(lNotifications.list, function(pIdx, pVal){
		pVal.new=false;
	});
	saveNotifications(lNotifications);
}
function showNewCount(pCount) {
	$('.notifications-menu span.badge').html(pCount>0?pCount:"");
}
function showAlert(pType, pMessage, pDur) {
	divAlert$.removeClass('alert-success alert-info alert-danger alert-warning');
	divAlert$.addClass('alert-'+pType);
	divAlert$.children('span').html(pMessage);
	divAlert$.show();
	if (pDur)
		setTimeout(hideAlert, pDur);
}
function hideAlert() {
	divAlert$.hide();
}
/**** Notifications ****/

/**** Utilities ****/
function getDisplayDate(pMillis) {
	return dateFormatter.formatDate(new Date(pMillis+ISTOFFSET));
}
function getDisplayTime(pMillis) {
	return timeFormatter.formatDate(new Date(pMillis+ISTOFFSET));
}
function getDisplayDateTime(pMillis) {
	var lDate = new Date(pMillis+ISTOFFSET);
	return dateFormatter.formatDate(lDate) + ' ' + timeFormatter.formatDate(lDate);
}
function setImage(pImage$, pPath, pImgFile) {
	if (!pImgFile) {
		pImage$.hide();
		return;
	}
	var lImgPath = pPath + pImgFile;
	pImage$.error(function(){
		pImage$.unbind('error');
		pImage$.attr('src','');
		pImage$.hide();
	});
	pImage$.show();
	pImage$.attr('src',lImgPath);
}
function saveToSession(pKey,pData)
{
	if(typeof(Storage) !== "undefined") {
		sessionStorage.setItem(pKey,JSON.stringify(pData));
	}
	return null;
}
function getFromSession(pKey)
{
	if(typeof(Storage) !== "undefined") {
		var lData = sessionStorage.getItem(pKey);
		if(lData == null || lData == "")return null;
		return JSON.parse(lData);
	}
	return null;
}
function downloadFile(pUrl,pBtn$,pPostData) {
	if (pBtn$) pBtn$.prop('disabled', true);
    var lXhr = new XMLHttpRequest();
    var lPost=pPostData!=null;
    public_vars.$pageLoadingOverlay.removeClass('loaded');
    lXhr.open(lPost?'POST':'GET', pUrl, true);
    lXhr.responseType = 'arraybuffer';
    lXhr.setRequestHeader("loginKey", loginData==null?null:loginData.loginKey);
    lXhr.onload = function () {
    	public_vars.$pageLoadingOverlay.addClass('loaded');
        if (this.status === 200) {
            var lFilename = "";
            var lDisposition = lXhr.getResponseHeader('Content-Disposition');
            if (lDisposition && lDisposition.indexOf('attachment') !== -1) {
                var lFilenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                var lMatches = lFilenameRegex.exec(lDisposition);
                if (lMatches != null && lMatches[1]) lFilename = lMatches[1].replace(/['"]/g, '').trim();
            }
            var lType = lXhr.getResponseHeader('Content-Type');

            var lBlob = new Blob([this.response], { type: lType });
            if (typeof window.navigator.msSaveBlob !== 'undefined') {
                // IE workaround for "HTML7007: One or more blob URLs were revoked by closing the blob for which they were created. These URLs will no longer resolve as the data backing the URL has been freed."
                window.navigator.msSaveBlob(lBlob, lFilename);
            } else {
                var URL = window.URL || window.webkitURL;
                var lDownloadUrl = URL.createObjectURL(lBlob);

                if (lFilename) {
                    // use HTML5 a[download] attribute to specify filename
                    var a = document.createElement("a");
                    // safari doesn't support this yet
                    if (typeof a.download === 'undefined') {
                        window.location = lDownloadUrl;
                    } else {
                        a.href = lDownloadUrl;
                        a.download = lFilename;
                        document.body.appendChild(a);
                        a.click();
                    }
                } else {
                    window.location = lDownloadUrl;
                }

                setTimeout(function () { URL.revokeObjectURL(lDownloadUrl); }, 100); // cleanup
            }
        } else {
        	alert("Download failed with error code " + this.status);
        }
        if (pBtn$) pBtn$.prop('disabled', false);
    };
    if (lPost)
    	lXhr.send(pPostData);
    else
    	lXhr.send();
}
Handlebars.registerHelper('ifHasAccess', function (pSecKey, options) {
	if (loginData.secKeys[pSecKey]!=null) return options.fn(this);
	else options.inverse(this);
});
var handleBarDecFormatter = new NumberFormatter("##,##,##,##0.00##");
Handlebars.registerHelper('formatDec', function(options) {
  return new Handlebars.SafeString(handleBarDecFormatter.formatNumber(options.fn(this)));
});

Handlebars.registerHelper('encodeURIComponent', function(url) {
	return encodeURIComponent(url);
});

function showPdf(pUrl){
	location.href =pUrl+'?loginKey='+loginData.loginKey;
}
/**** Utilities ****/
