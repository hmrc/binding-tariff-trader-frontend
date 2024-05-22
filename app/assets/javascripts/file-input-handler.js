function toggleSubmitButton(enabled) {
    if(enabled) {
        if(document.getElementById("submit").attributes.getNamedItem("disabled") !== null) {
            document.getElementById("submit").removeAttribute("disabled");
            document.getElementById("submit").classList.remove("govuk-button--disabled");
        }
    } else {
        if(document.getElementById("submit").attributes.getNamedItem("disabled") === null) {
            document.getElementById("submit").setAttribute("disabled", "disabled");
            document.getElementById("submit").classList.add("govuk-button--disabled");
        }
    }
}

function onFileSelected(id, targetURL) {
    return function(event) {
        const files = event.target.files;

        const csrfToken = document.querySelector('input[name="csrfToken"]').value;

        if (files.length > 0) {
            toggleSubmitButton(false);
            const xhr = new XMLHttpRequest();

            xhr.open("POST", targetURL, true);

            xhr.setRequestHeader('Csrf-Token', csrf);
            xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');

            xhr.onload = function () {
                toggleSubmitButton(true);
            };

            xhr.onerror = function() {
                toggleSubmitButton(true);
            };

            const request = JSON.stringify({
                id: id,
                name: files[0].name,
                mimeType: files[0].type,
                size: files[0].size
            });

            xhr.send(request);
        }
    }
}