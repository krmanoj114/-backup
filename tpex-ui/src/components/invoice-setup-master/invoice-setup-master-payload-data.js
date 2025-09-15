export const getInvoiceSetupSavePayload = i => {

    return (
        {
            "cfc": (i.cfc).toString(),
            "exporterCode": (i.exporterCode).toString(),
            "reExpCode": (i.reExpCode).toString(),
            "lineCode": (i.lineCode).toString(),
            "packingMonth": i.packingMonth,
            "priceMethod": i.priceMethod==='PCKMNTH'?'Packing Month':'ETD',
            "vanDateFrom": i.vanDateFrom,
            "vanDateTo": i.vanDateTo
          });
    };
