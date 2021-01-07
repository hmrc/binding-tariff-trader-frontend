function onFileSelected(id, targetURL, csrf) {
    return function(event) {
        const files = event.target.files;

        if (files.length > 0) {
            $('#submit').attr('disabled', true);

            const xhr = new XMLHttpRequest();

            xhr.open("POST", targetURL, true);

            xhr.setRequestHeader('Csrf-Token', csrf);
            xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8')

            xhr.onload = function () {
                $('#submit').attr('disabled', false);
            };

            const request = JSON.stringify({
                id: id,
                name: files[0].name,
                mimeType: files[0].type,
                size: files[0].size
            });

            try {
                xhr.send(request);
                if (xhr.status != 200) {
                    console.log(xhr.status);
                } else {
                    alert(xhr.response);
                }
            } catch(err) { // instead of onerror
                $('#submit').attr('disabled', false);
                reject(new TypeError('Network request failed'));
            }
        }
    }
}