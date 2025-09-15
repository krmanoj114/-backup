export function getDeletedPayload(isCheck, primaryKey, userId, tableName, moduleName, rows, editDataForPaload = {}) {
    if (moduleName === "RENBANE_GROUP_CODE_MASTER") {
        const getDeletedIds = Object.keys(editDataForPaload);
        const editDataSelected = rows.filter((r) => {
            return getDeletedIds.includes(r.id.toString())
        }).map((e) => {
            let groupCodes = [...e.renGroupCode1.split(','), ...e.renGroupCode2.split(','), ...e.renGroupCode3.split(','), ...e.renGroupCode4.split(','), ...e.renGroupCode5.split(',')];
            let allGroupCode = groupCodes.filter((e) => e); 

            e.contGrpCd = allGroupCode;
            delete e.folderName1;
            delete e.folderName2;
            delete e.folderName3;
            delete e.folderName4;
            delete e.folderName5;
            delete e.renGroupCode1;
            delete e.renGroupCode2;
            delete e.renGroupCode3;
            delete e.renGroupCode4;
            delete e.renGroupCode5;
            delete e.id;

           return e;
        });
        return {
            data : editDataSelected,
        };

    } else {
        return {
            data: isCheck.map(d => {
                return { [primaryKey]: d }
            }),
            userId,
            primaryKey,
            tableName
        }
    }
}