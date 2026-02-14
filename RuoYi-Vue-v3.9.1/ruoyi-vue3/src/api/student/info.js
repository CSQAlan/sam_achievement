import request from "@/utils/request";

export function getStudentInfoByNo(no) {
  return request({
    url: "/student/student/list",
    method: "get",
    params: { no },
  }).then((res) => ({
    code: res?.code ?? 200,
    data: Array.isArray(res?.rows) ? res.rows[0] : null,
    msg: res?.msg,
  }));
}

export function updateStudentInfo(data) {
  const no = data?.no || data?.userName;
  if (!no) {
    return request({
      url: "/student/student",
      method: "put",
      data,
    });
  }

  return request({
    url: "/student/student/list",
    method: "get",
    params: { no },
  }).then((res) => {
    const row = Array.isArray(res?.rows) ? res.rows[0] : null;
    if (row?.studentId) {
      return request({
        url: "/student/student",
        method: "put",
        data: { ...row, ...data, studentId: row.studentId },
      });
    }
    return request({
      url: "/student/student",
      method: "put",
      data,
    });
  });
}
