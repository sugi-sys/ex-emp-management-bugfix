'use strict';
$(function () {
    const url = 'https://zipcoda.net/api';

    $('#zip-code').on('input', () => {
        const inputDigits = $('#zip-code').val().length;
        const maxDigits = 7;
        if (inputDigits == maxDigits) {
            $.ajax({
                url: url,
                type: 'get',
                dataType: 'json',
                data: {
                    zipcode: $('#zip-code').val()
                }
            }).done((data) => {
                $('#address').val(data.items[0].pref + data.items[0].address);
            }).fail(() => {

            });
        }
    })
})