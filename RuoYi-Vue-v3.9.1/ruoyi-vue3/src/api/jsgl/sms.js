import request from "@/utils/request";

export function sendCodePhoneApi(data) {
  return request({
    url: "/jsgl/sms/sendCode",
    method: "post",
    data,
  });
}

export function verifyCodePhoneApi(data) {
  return request({
    url: "/jsgl/sms/verifyCode",
    method: "post",
    data,
  });
}
