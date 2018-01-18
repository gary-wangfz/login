import axios from 'axios'
import Qs from 'qs'
import queryString from 'query-string';

var localConfig = {
    isMock: false,
    isDev: true,
    apiHost: "https://dev.example.com/api/",
};
var testConfig = {
    isMock: false,
    apiHost: "https://dev.example.com/api/",
};
var publishConfig = {
    isMock: false,
    apiHost: "http://example.com/api/",
};

var API = {
    config: localConfig
};
if (__DEV__) {
    API.config = localConfig;
} else if (__TEST__) {
    API.config = testConfig;
} else if (__PROD__) {
    API.config = publishConfig;
}


var instance = axios.create({
    baseURL: API.config.apiHost,
    withCredentials: true,
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
    },
});
// 全局登录拦截拦截
instance.interceptors.response.use(
    response => {
        if (response.data.errorCode == 2) {
            if (location.href.indexOf('login.html') > 0) {
                return response.data
            }
            sessionStorage.setItem('gotoBeforeUrl', location.href);
            location.href = './login.html';
            return
        } else {
            sessionStorage.removeItem('gotoBeforeUrl')
        }
        return response.data
    });

const AppService = {
    getRequest: (url, data) => {
        return instance.get(url, data ? {params: data} : {})
    },
    postRequest: (url, data) => {
        data = data ? Qs.stringify(data) : null;
        return instance.post(url, data)
    },
    putRequest: (url, data) => {
        if (data) {
            url = url + '?' + queryString.stringify(data)
        }
        return instance.put(url)
    },
    deleteRequest: (url, data) => {
        return instance.delete(url, data ? {params: data} : {})
    },
    patchRequest: (url, data) => {
        if (data) {
            url = url + '?' + queryString.stringify(data)
        }
        return instance.patch(url, data)
    }
};

export default AppService