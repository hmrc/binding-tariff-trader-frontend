uploadFileNameSpace = ( function() {

    function beforeSubmit(e) {

        if (e.preventDefault) e.preventDefault();

        /* do what you want with the form */
        fileList.postAll();

        // // You must return false to prevent the default form behavior
        //return false;
    }

    fileList = (function () {

        var _files = [];

        var _tableId;

        var readableSize = function formatBytes(bytes) {
            var decimals = 2
            if (bytes == 0) return '0 Bytes';
            var k = 1024,
                dm = decimals <= 0 ? 0 : decimals || 2,
                sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
                i = Math.floor(Math.log(bytes) / Math.log(k));
            return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
        };

        var fileDoesntExist = function (file) {
            var notExist = true;
            _files.forEach(function (f) {
                if (f.name == file.name)
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
                    alert('File upload successful')
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    alert(jqXHR.status)
                }
            });
        }

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
                    var href = "javascript:fileList.delete('" + f.name + "')"
                    var divFile =
                        '<div class="file-list__file" id="' + f.uid + '">\n' +
                        '    <div class="file-list__name"><span>' + f.name + '</span></div>\n' +
                        '    <div class="file-list__size">' + readableSize(f.size) + '</div>\n' +
                        '    <div class="file-list__action"><a id="" class="remove" href="' + href + '" title="' + f.name + '">Remove</a></div>\n' +
                        '</div>'

                    $('#' + _tableId).append(divFile)
                });
            }
        }

    })();

    return {
        initialize: function (formId, inputFileId, tableId){

            //  var form = document.getElementById(formId);
            //  form.addEventListener("submit", beforeSubmit);

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

})();