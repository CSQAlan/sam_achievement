import request from "@/utils/request";

export function generateWechatQRCode() {
  return request({
    url: "/common/wx/qrcode",
    method: "get",
  });
}

export function getConfirQrcode(username) {
  return request({
    url: `/common/wx/getConfirQrcode?username=${username}`,
    method: "get",
  });
}

export function checkWechatBind(uuid) {
  return request({
    url: `/common/wx/check-bind?uuid=${uuid}`,
    method: "get",
  });
}

export function unbindWechat() {
  return request({
    url: "/common/wx/unbind",
    method: "post",
  });
}
