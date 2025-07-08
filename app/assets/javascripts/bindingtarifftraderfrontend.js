// Find first ancestor of el with tagName
// or undefined if not found
function upTo(el, tagName) {
    tagName = tagName.toLowerCase();

    while (el && el.parentNode) {
      el = el.parentNode;
      if (el.tagName && el.tagName.toLowerCase() == tagName) {
        return el;
      }
    }

    // Many DOM methods return null if they don't
    // find the element they are searching for
    // It would be OK to omit the following and just
    // return undefined
    return null;
  }

$(document).ready(function () {

    // =====================================================
    // Initialise show-hide-content
    // Toggles additional content based on radio/checkbox input state
    // =====================================================
    var showHideContent, mediaQueryList;
    showHideContent = new GOVUK.ShowHideContent()
    showHideContent.init()

    // =====================================================
    // Initialise link as button polyfill
    // Adds button functionality to links styled as buttons
    // =====================================================
    GOVUK.shimLinksWithButtonRole.init()

    updateSessionHistory();

    // =====================================================
    // Back link uses a js session storage based history stack
    // =====================================================
    // store referrer value to cater for IE - https://developer.microsoft.com/en-us/microsoft-edge/platform/issues/10474810/  */
    var docReferrer = document.referrer
    // prevent resubmit warning
    if (window.history && window.history.replaceState && typeof window.history.replaceState === 'function') {
        window.history.replaceState(null, null, window.location.href);
    }
    // back click handle, dependent upon presence of referrer & no host change
    $('#back-link').on('click', function (e) {
        e.preventDefault();
        if (window.history && window.history.back && typeof window.history.back === 'function' &&
            (docReferrer !== "" && docReferrer.indexOf(window.location.host) !== -1)) {
            const historyStack = JSON.parse(sessionStorage.getItem("historyStack")) || [];
            if (historyStack.length > 1) {
                historyStack.pop();    //take the url for page just exited off the stack
                const previousPath = historyStack[historyStack.length - 1];
                window.location.href = window.location.origin + previousPath;
                sessionStorage.setItem("historyStack", JSON.stringify(historyStack));
            }
        }
    })

    //======================================================
    // Move immediate focus to any error summary
    //======================================================
    if ($('.error-summary a').length > 0) {
        $('.error-summary').focus();
    }

    function updateSessionHistory() {
        function isEmpty(stack) {
            return stack.length === 0
        }

        function last(stack) {
            return stack[stack.length - 1]
        }

        const historyStack = JSON.parse(sessionStorage.getItem("historyStack")) || [];
        const currentPath = window.location.pathname;

        if (isEmpty(historyStack) || currentPath !== last(historyStack)) {
            historyStack.push(currentPath)
            sessionStorage.setItem("historyStack", JSON.stringify(historyStack));
        }
    }

    // =====================================================
    // Adds data-focuses attribute to all containers of inputs listed in an error summary
    // This allows validatorFocus to bring viewport to correct scroll point
    // =====================================================
    function assignFocus() {
        var counter = 0;
        $('.error-summary-list a').each(function () {
            var linkhash = $(this).attr("href").split('#')[1];
            $('#' + linkhash).parents('.form-field, .form-group').first().attr('id', 'f-' + counter);
            $(this).attr('data-focuses', 'f-' + counter);
            counter++;
        });
    }

    assignFocus();

    function beforePrintCall() {
        if ($('.no-details').length > 0) {
            // store current focussed element to return focus to later
            var fe = document.activeElement;
            // store scroll position
            var scrollPos = window.pageYOffset;
            $('details').not('.open').each(function () {
                $(this).addClass('print--open');
                $(this).find('summary').trigger('click');
            });
            // blur focus off current element in case original cannot take focus back
            $(document.activeElement).blur();
            // return focus if possible
            $(fe).focus();
            // return to scroll pos
            window.scrollTo(0, scrollPos);
        } else {
            $('details').attr("open", "open").addClass('print--open');
        }
        $('details.print--open').find('summary').addClass('heading-medium');
    }

    function afterPrintCall() {
        $('details.print--open').find('summary').removeClass('heading-medium');
        if ($('.no-details').length > 0) {
            // store current focussed element to return focus to later
            var fe = document.activeElement;
            // store scroll position
            var scrollPos = window.pageYOffset;
            $('details.print--open').each(function () {
                $(this).removeClass('print--open');
                $(this).find('summary').trigger('click');
            });
            // blur focus off current element in case original cannot take focus back
            $(document.activeElement).blur();
            // return focus if possible
            $(fe).focus();
            // return to scroll pos
            window.scrollTo(0, scrollPos);
        } else {
            $('details.print--open').removeAttr("open").removeClass('print--open');
        }
    }

    //Chrome
    if (typeof window.matchMedia != 'undefined') {
        mediaQueryList = window.matchMedia('print');
        mediaQueryList.addListener(function (mql) {
            if (mql.matches) {
                beforePrintCall();
            }
            ;
            if (!mql.matches) {
                afterPrintCall();
            }
            ;
        });
    }

    //Firefox and IE (above does not work)
    window.onbeforeprint = function () {
        beforePrintCall();
    }
    window.onafterprint = function () {
        afterPrintCall();
    }

    if(document.getElementById("govuk-notification-banner--success")) {
         document.getElementById("govuk-notification-banner--success").focus()
    }

});

