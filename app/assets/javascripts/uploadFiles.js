var fileList = (function () {

    _files = [];
    var _tableId;

    var readableSize = function formatBytes(bytes) {
        var decimals = 2;
        if (bytes === 0) return '0 Bytes';
        var k = 1024,
            dm = decimals <= 0 ? 0 : decimals || 2,
            sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
            i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
    };

    var fileDoesntExist = function (file) {
        var notExist = true;
        _files.forEach(function (f) {
            if (f.name === file.name)
                notExist = false;
        });
        return notExist;
    };

    var postForm = function (fileForm) {

        $.ajax({
            url: '/binding-tariff-application/uploadSupportingMaterialMultiple',
            enctype: 'multipart/form-data',
            type: 'POST',
            data: fileForm,
            cache: false,
            contentType: false,
            processData: false,
            success: function (data, status, jqXHR) {
                // alert('File upload successful')
            },
            error: function (jqXHR, textStatus, errorThrown) {
                // alert(jqXHR.status)
            }
        });
    };

    var fileBoxDiv = function (name, size, errorMsg){

        var href = "javascript:fileList.delete('" + name + "')";

        var divClass = "file-list__file";
        if (errorMsg) divClass += " file-list__file-error";

        var output = '<div class="' + divClass + '" id="' + name + '">\n' +
            '    <div class="file-list__name"><span>' + name + '</span></div>\n' +
            '    <div class="file-list__size">' + readableSize(size) + '</div>\n' +
            '    <div class="file-list__action"><a id="" class="remove" href="' + href + '" title="' + name + '">Remove</a></div>\n';
        if (errorMsg) {
            output += '    <div class="file-list__status" aria-live="polite" aria-hidden="true">' + errorMsg + '</div>\n';
        }
        output +=     '</div>';

        return output;
    };

    return {
        init: function (tableId){
            _tableId  = tableId
        },
        add: function (file) {
            if (fileDoesntExist(file)) {
                _files.push(file);
            }
        },
        delete: function (name){

            for( var i = 0; i < _files.length; i++){
                if ( _files[i].name === name) {
                    _files.splice(i, 1);
                }
            }
            this.showListOn();
        },
        deleteFailed: function (name){
            for( var i = 0; i < _files.length; i++){
                if ( validateUploads.validate(_files[i])) {
                    _files.splice(i, 1);
                }
            }
            this.showListOn();
            $('#error-dialog').empty();
        },
        postAll: function () {
            var csrfToken = document.getElementsByName('csrfToken')[0].value;
            var form = new FormData();
            form.append("csrfToken", csrfToken);
            _files.forEach(function (f) {
                form.append(f.name, f);
            });
            postForm(form)
        },
        showListOn: function () {
            $('#' + _tableId).empty();
            _files.forEach(function (f) {
                $('#' + _tableId).append(fileBoxDiv(f.name,f.size,f.errorMsg))
            });
        },
        validate: function () {
            var isValidForm = true;
            _files.forEach(function (f) {
                var errorMsg = validateUploads.validate(f)
                if (errorMsg) {
                    isValidForm = false;
                    f.errorMsg = errorMsg
                } else {
                    f.errorMsg = ""
                }
            });
            this.showListOn();
            return isValidForm;
        }
    }

})();

var validateUploads = ( function() {

    var valid_extensions = ['jpg', 'png'];

    var hasInvalidSize = function (file) {
        // Less than 10 MB
        return file.size > 10485760;
    };

    var hasInvalidExtension = function (file) {
        var ext = file.name.split('.').pop();
        return !valid_extensions.includes(ext);
    };

    return {
        validate: function (file) {

            if (hasInvalidExtension(file)) {
                return "Your document will not upload because it's in the wrong format";
            }

            if (hasInvalidSize(file)) {
                return "Your document will not upload because it's bigger than 10MB";
            }

            return "";

        },
        showErrors: function () {
            var errorDiv =
                '      <div class="error-summary error-summary--show" role="group" aria-labelledby="error-summary-heading" tabindex="-1">\n' +
                '            <h2 class="heading-medium error-summary-heading" id="error-summary-heading">\n' +
                '                There were problems with some documents\n' +
                '            </h2>\n' +
                '            <p>You need to remove the documents to continue</p>\n' +
                '            <a id="error" href="javascript:fileList.deleteFailed()">Remove all failed documents</a>\n' +
                '        </div>\n';

            $('#error-dialog').html(errorDiv);
        }
    }

})();


var uploadFileNameSpace = ( function() {

    function beforeSubmit(e) {

        // Validate and prevent submition in case of invalid files
        if (!fileList.validate()){
            if (e.preventDefault) e.preventDefault();
            validateUploads.showErrors();
            return false;
        }

        /* Post all the files to upscan */
        fileList.postAll();

    };

    return {
        initialize: function (formId, inputFileId, tableId){

            var form = document.getElementById(formId);
            form.addEventListener("submit", beforeSubmit);

            var multipleFileInput = document.getElementById(inputFileId);

            multipleFileInput.addEventListener('change', function (event) {
                for (var i = 0; i < multipleFileInput.files.length; i++) {
                    fileList.add(multipleFileInput.files[i]);
                }
                fileList.init(tableId)
                fileList.showListOn()
            });

        }
    }

    $( document ).ready(function() {
        uploadFileNameSpace.initialize("upload-files-form","multiple-file-input","list-of-files-table");
    });

})();

