export const getReturnablePackingSavePayload = i => {
    return (
        {
            "packingPlant": i.packingPlant,  
            "importerCode": i.importerCode,
            "moduleType": i.moduleType,  
            "moduleDesciption": i.moduleDesciption,  
            "vanningDateFrom": i.vanningDateFrom,  
            "vanningDateTo": i.vanningDateTo        
          });
    };

export const getReturnablePackingDeletePayload = i =>{
    return ({
        "importerCode": i.importerCode,
        "moduleType": i.moduleType,
        "vanningDateFrom": i.vanningDateFrom,
        "packingPlant": i.packingPlant
    });
};