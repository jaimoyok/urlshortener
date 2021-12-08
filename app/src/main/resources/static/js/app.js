$(document).ready(
    function() {
        $("#shortener").submit(
            function(event) {
                event.preventDefault();
                //var withQR = $("#qr").is(":checked")
                $.ajax({
                    type : "POST",
                    url : "/api/link",
                    data : {url: $("#url").val(), qr: true} ,
                    success : function(data) {
                        $("#result").html(
                            "</p></p></p> <div class='center-block' <h2>El LINK generado es:</h2>" + "<svg xmlns='http://www.w3.org/2000/svg' style='display: none;'> <symbol id='check-circle-fill' fill='currentColor' viewBox='0 0 16 16'> <path d='M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-3.97-3.03a.75.75 0 0 0-1.08.022L7.477 9.417 5.384 7.323a.75.75 0 0 0-1.06 1.06L6.97 11.03a.75.75 0 0 0 1.079-.02l3.992-4.99a.75.75 0 0 0-.01-1.05z'/> </symbol></svg>" +
                            "<div class='alert alert-info' role='alert'> <svg class='bi flex-shrink-0 me-2' width='24' height='48' role='img' aria-label='Success:'><use xlink:href='#check-circle-fill'/></svg> <div> <a target='_blank' href='"
                            + data.url
                            + "'>"
                            + data.url
                            + "</a></div></div></div>");

                        $("#result").append(
                            "</p></p></p> <div class='center-block' <h2>El QR generado es:</h2>" + "<svg xmlns='http://www.w3.org/2000/svg' style='display: none;'> <symbol id='check-circle-fill' fill='currentColor' viewBox='0 0 16 16'> <path d='M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-3.97-3.03a.75.75 0 0 0-1.08.022L7.477 9.417 5.384 7.323a.75.75 0 0 0-1.06 1.06L6.97 11.03a.75.75 0 0 0 1.079-.02l3.992-4.99a.75.75 0 0 0-.01-1.05z'/> </symbol></svg>" +
                            "<div class='alert alert-info' role='alert'> <svg class='bi flex-shrink-0 me-2' width='24' height='48' role='img' aria-label='Success:'><use xlink:href='#check-circle-fill'/></svg> <div> <a target='_blank' href='"
                            + data.qr
                            + "'>"
                            + data.qr
                            + "</a></div></div></div>");
                    },
                    error : function() {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
    });
