function priortyArr(k){
    return ((k.length === 1 && k[0] === '') || k==='') ? [] : k
}
export const getMixPrivilegeSavePayload = i => {

    return (
        {
            "privMstId": i.privMstId,
            "destCode": i.destCode,
            "crFmlyCode": i.crFmlyCode,
            "reExpCode": (i.reExporterCode).split('-')[0],
            "effFromDate": i.effFrom,
            "effToDate": i.effTo,
            "priorityOne": priortyArr(i.priorityOne),
            "priorityTwo": priortyArr(i.priorityTwo),
            "priorityThree": priortyArr(i.priorityThree),
            "priorityFour": priortyArr(i.priorityFour),
            "priorityFive": priortyArr(i.priorityFive),
            "companyCode": "TMT",
            "confirmFlag":''
          });
    };
