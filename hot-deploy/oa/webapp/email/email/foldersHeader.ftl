<div>support@rextec.com.cn 您的邮箱存储空间是：1024MB，您已使用了 10% 的空间。</div>
<div id="storagePercentage">
    <div class="progress-label">Loading...</div>
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
        left: 50%;
        top: 4px;
        font-weight: bold;
        text-shadow: 1px 1px 0 #fff;
    }
</style>
<script type="text/javascript">
    $(function(){
        var progressbar = $( "#storagePercentage" ),
                progressLabel = $( ".progress-label" );

        progressbar.progressbar({
            value: false,
            change: function() {
                progressLabel.text( progressbar.progressbar( "value" ) + "%" );
            }
        });

        function progress() {
            progressbar.progressbar( "value", 10 );
        }

        setTimeout( progress, 1000 );
    });
</script>