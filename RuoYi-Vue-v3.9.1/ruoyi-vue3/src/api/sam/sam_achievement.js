import request from "@/utils/request";

export function getTeacherInfoByNo(no) {
  return request({
    url: "/achievement/teacher/list",
    method: "get",
    params: { no },
  }).then((res) => ({
    code: res?.code ?? 200,
    data: Array.isArray(res?.rows) ? res.rows[0] : null,
    msg: res?.msg,
  }));
}

export function updateTeacherInfo(data) {
  const no = data?.no || data?.userName;
  if (!no) {
    return request({
      url: "/achievement/teacher",
      method: "put",
      data,
    });
  }

  return request({
    url: "/achievement/teacher/list",
    method: "get",
    params: { no },
  }).then((res) => {
    const row = Array.isArray(res?.rows) ? res.rows[0] : null;
    if (row?.id) {
      return request({
        url: "/achievement/teacher",
        method: "put",
        data: { ...row, ...data, id: row.id },
      });
    }
    return request({
      url: "/achievement/teacher",
      method: "put",
      data,
    });
  });
}
