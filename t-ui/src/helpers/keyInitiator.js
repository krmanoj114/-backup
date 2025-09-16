export default class Initiator{
constructor(name, defaultValue) {
    this.name = name;
    this.defaultValue = defaultValue;
    this.data = {};
    this.data[name] = {
      isLoading: false,
      error: undefined,
      data: defaultValue,
    };
  }

  setLoading(loading, defaultValue) {
    this.data[this.name].isLoading = loading;
    if (!this.data[this.name].data) {
      this.data[this.name].data = defaultValue;
    }
    return { ...this.data[this.name] };
  }

  setData(data) {
    this.data[this.name].isLoading = false;
    this.data[this.name].data = data;
    this.data[this.name].error = undefined;
    return { ...this.data[this.name] };
  }

  setError(error) {
    this.data[this.name].isLoading = false;
    this.data[this.name].data = this.defaultValue;
    this.data[this.name].error = {
      errorMessage: "Application Error",
      description: error,
    };
    return { ...this.data[this.name], error: error.response.data, isOffline: false };
  }

  getState() {
    return this.data[this.name];
  }
}


