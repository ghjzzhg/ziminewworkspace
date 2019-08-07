<#macro WorkPlanProgressbar id value=0>
    <div id="${id}">
        <div id="progress-label_${id}" class="progress-label"></div>
    </div>
    <style type="text/css">
        #storagePercentage{
            margin: 10px 0;
        }
        .ui-progressbar {
            position: relative;
        }
        .progress-label {
            position: absolute;
            left: 40%;
            top: 4px;
            font-weight: bold;
            text-shadow: 1px 1px 0 #fff;
        }
    </style>
    <script type="text/javascript">
        $(function(){
            var progressbar = $("#${id}"),
                    progressLabel = $( "#progress-label_${id}" );
            progressbar.progressbar({
                value: false,
                change: function() {
                    progressLabel.text( progressbar.progressbar( "value" ) + "%" );
                }
            });
            function progress() {
                progressbar.progressbar( "value",${value});
            }

            setTimeout(progress, 1000);
        });
    </script>
</#macro>
