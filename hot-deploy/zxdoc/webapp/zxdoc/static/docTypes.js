(function () {

    var DocTypes = {
            1: {
                'A': [
                    {code: '10001', name: '财务'},
                    {code: '10002', name: '财务'}],
                'B': [
                    {code: '20001', name: '法律'},
                    {code: '20002', name: '法律'}]
            },
            10001: {
                1000101: '子类1',
                1000102: '子类2'
            },
            1000101: {
                100010101: '细分1',
                100010102: '细分2'
            },
            10002: {
                1000201: '子类1',
                1000202: '子类2'
            }
        }
        ;

    if (typeof window !== 'undefined') {
        window.DocTypes = DocTypes;
    }

    return DocTypes;

})();
