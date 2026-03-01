import request from "./request.js";

/**
 * 获取生成的微信绑定二维码
 * 
 */
export function generateWechatQRCode() {
  return request({
    url: `/common/wx/qrcode`,
    method: "get",
  });
}
/**
 * 获取生成的微信确认二维码
 * @param {string} username 用户名
 */
export function getConfirQrcode(username) {
  return request({
    url: `/common/wx/getConfirQrcode?username=${username}`,

    method: "get",
  });
}


/**
 * 轮询检查微信绑定结果
 * @param {string} uuid 
 */
export function checkWechatBind(uuid) {
  return request({
    url: `/common/wx/check-bind?uuid=${uuid}`,
    method: "get",
  });
}
 
/**
 * 解除微信绑定
 *
 */
export function unbindWechat() {
  return request({
    url: `/common/wx/unbind`,
    method: "post",
  });
}