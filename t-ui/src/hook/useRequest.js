import { useState, useEffect } from "react"; 
import Initiator from "../helpers/keyInitiator";

export default function useGetRequest(callBack, param = [], defaultValue = undefined) {
  const result = new Initiator(useGetRequest.name, defaultValue);
  const [data, setData] = useState(null);
  useEffect(() => {
    let isRendered = true;
    (async () => {
      try {
        data && setData(null);
        const response = await callBack();
        if (response && isRendered) {
          const resultData = await response;
          setData(result.setData(resultData || defaultValue));
        } else if (isRendered) {
          setData(result.setLoading(false, defaultValue));
        }
      } catch (error) {
        isRendered && setData(result.setError(error));
      }
    })();

    return () => {
      isRendered = false;
    };
  }, [...param]);
  return data || result.setLoading(true, defaultValue);
}

export function usePostRequest(callBackHandler, successCallBack, defaultValue) {
  const result = new Initiator(useGetRequest.name, defaultValue);
  const [data, setData] = useState(null);
  const callback = (callbackPara) => {
    myCall(true, 1, callbackPara);
  };
  const myCall = async (isRendered, count, callbackPara) => {
    try {
      data
        ? setData(null)
        : setData({
            complete: false,
            ...result.setLoading(true, defaultValue),
          });

      const response = await callBackHandler(callbackPara);

      if (response && isRendered) {
        const resultData = await response;
        setData({
          complete: true,
          ...result.setData(resultData || defaultValue),
        });
        successCallBack &&
          successCallBack({
            complete: true,
            ...result.setData(resultData || defaultValue),
          });
      } else if (isRendered) {
        setData(result.setLoading(false, defaultValue));
      }
    } catch (error) {
      isRendered && setData(result.setError(error));
    }
  };

  const callresult = data || result.setLoading(false, defaultValue);


  return { callback, ...callresult };
}

