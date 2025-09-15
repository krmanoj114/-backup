export const getShippingControlSavePayload = i => {

    return (
        {
            "buyer": i.buyer,
            "impCode": i.impCode,
            "expCode": i.expCode,
            "cfcCode": i.cfcCode,
            "series": i.series,
            "setPartCode": i.setPartCode,
            "portOfDischarge": i.portOfDischarge,
            "productGroup": i.productGroup,
            "folderName": i.folderName,
            "consignee": i.consignee,
            "notifyParty": i.notifyParty,
            "goodDesc1": i.goodDesc1,
            "goodDesc2": i.goodDesc2,
            "goodDesc3": i.goodDesc3,
            "tradeTerm": i.tradeTerm,
            "certificationOfOriginReport": i.certificationOfOriginReport,
            "soldToMessrs": i.soldToMessrs,
            "plsFlag": i.plsFlag,
            "updateByUserId": "Test User",
            "isNewRow": i.isNewRow
          });
    };

export const getShippingControlDeletePayload = i => {
    return({
        "impCode": i.impCode,
        "expCode": i.expCode,
        "cfcCode": i.cfcCode,
        "series": i.series,
        "setPartCode": i.setPartCode,
        "portOfDischarge": i.portOfDischarge
    });
};
