import request from "@/utils/request";

export function sendCodeApi(data) {
  return request({
    url: "/jsgl/email/sendCode",
    method: "post",
    data,
  });
}

export function verifyCodeEmailApi(data) {
  return request({
    url: "/jsgl/email/verifyCode",
    method: "post",
    data,
  });
}
