// index page 로딩 후 전국의 시도 설정.
let serviceKey = "--------- 본인의 Service Key를 넣으세요 ---------";
let areaUrl =
  "https://apis.data.go.kr/B551011/KorService1/areaCode1?serviceKey=" +
  serviceKey +
  "&numOfRows=20&pageNo=1&MobileOS=ETC&MobileApp=AppTest&_type=json";

// fetch(areaUrl, { method: "GET" }).then(function (response) { return response.json() }).then();
fetch(areaUrl, { method: "GET" })
  .then((response) => response.json())
  .then((data) => makeOption(data));
function makeOption(data) {
  let areas = data.response.body.items.item;
  // console.log(areas);
  let sel = document.getElementById("search-area");
  areas.forEach((area) => {
    let opt = document.createElement("option");
    opt.setAttribute("value", area.code);
    opt.appendChild(document.createTextNode(area.name));

    sel.appendChild(opt);
  });
}

// 검색 버튼을 누르면..
// 지역, 유형, 검색어 얻기.
// 위 데이터를 가지고 공공데이터에 요청.
// 받은 데이터를 이용하여 화면 구성.
document.getElementById("btn-search").addEventListener("click", () => {
  let searchUrl = `https://apis.data.go.kr/B551011/KorService1/searchKeyword1?serviceKey=${serviceKey}&numOfRows=10&pageNo=1&MobileOS=ETC&MobileApp=AppTest&_type=json&listYN=Y&arrange=A`;

  let areaCode = document.getElementById("search-area").value;
  let contentTypeId = document.getElementById("search-content-id").value;
  let keyword = document.getElementById("search-keyword").value;

  if (parseInt(areaCode)) searchUrl += `&areaCode=${areaCode}`;
  if (parseInt(contentTypeId)) searchUrl += `&contentTypeId=${contentTypeId}`;
  if (!keyword) {
    alert("검색어 입력 필수!!!");
    return;
  } else searchUrl += `&keyword=${keyword}`;

  console.log(searchUrl);

  fetch(searchUrl)
    .then((response) => response.json())
    .then((data) => makeList(data));
});

function makeList(data) {
  // console.log(data);
  let trips = data.response.body.items.item;
  let tripList = ``;
  trips.forEach((area) => {
    tripList += `
      <tr>
        <td><img src="${area.firstimage}" width="100px"></td>
        <td>${area.title}</td>
        <td>${area.addr1} ${area.addr2}</td>
        <td>${area.mapy}</td>
        <td>${area.mapx}</td>
      </tr>
    `;
  });
  document.getElementById("trip-list").innerHTML = tripList;
}

// index page 로딩시 설문조사 실시여부 검사.
let storagePoll = localStorage.getItem("poll");
let pollContent;
if (storagePoll) {
  var poll = JSON.parse(storagePoll);
  var sdate = poll.sdate;
  var edate = poll.edate;
  var question = poll.question;
  var answers = poll.answers;
  pollContent = `
        <div class="text-center text-secondary mb-3"><h5>[ 투표해주세요 ]</h5></div>
        <div class="fw-bold text-info bg-light"><h6>${question}</h6></div>
        <div class="text-start p-3">
            <ul class="nav flex-column">
        `;
  answers.forEach(function (answer) {
    pollContent += `
              <li class="nav-item">
                  <label><input type="radio" name="poll-answer" value="${answer}" /> ${answer}</label>
              </li>
            `;
  });
  pollContent += `
            </ul>
        </div>
        <div class="text-end">
            <button class="btn btn-outline-primary btn-sm" id="btn-poll-action">투표하기</button>
            <button class="btn btn-outline-danger btn-sm" id="btn-poll-result">결과보기</button>
        </div>
        <div class="text-end text-secondary mt-1">투표기간 : ${sdate} ~ ${edate}</div>
        `;
  document.querySelector("#poll-area").innerHTML = pollContent;
  // 설문조사 투표하기!!!
  document.querySelector("#btn-poll-action").addEventListener("click", function () {
    let selItem = document.querySelector("input[name=poll-answer]:checked");
    if (selItem) alert(selItem.value + "을 선택했습니다.");
    else alert("답변항목 하나 이상을 선택해 주세요.");
  });
} else {
  pollContent = `<h3>진행중인 투표가 없습니다.</h3>`;
  document.querySelector("#poll-area").innerHTML = pollContent;
}

// 설문조사 모달창
// 설문조사 답변항목 추가.
document.querySelector("#btn-answer-add").addEventListener("click", function () {
  let answerListDiv = document.querySelector("#poll-answer-list");
  let answerItem = `
  <div class="row mb-1 poll-answer-list-item">
    <div class="col-md-10">
      <input type="text" class="form-control" name="answer">
    </div>
    <div class="col-md-2">
      <button type="button" class="btn btn-outline-danger btn-remove" onclick="javascript:removeAnswer(this);"><i class="bi bi-x-square-fill"></i> 삭제</button>
    </div>
  </div>`;
  answerListDiv.innerHTML = answerListDiv.innerHTML += answerItem;
});

// 설문조사 답변항목 삭제.
function removeAnswer(el) {
  let parentEl = el.parentNode.parentNode;
  document.querySelector("#poll-answer-list").removeChild(parentEl);
}

// 설문조사 생성.
document.querySelector("#btn-poll-create").addEventListener("click", function () {
  let sdate = document.querySelector("#start-date").value;
  let edate = document.querySelector("#end-date").value;
  if (!sdate || !edate) {
    alert("설문기간 입력!!!");
    return;
  }
  let question = document.querySelector("#question").value;
  if (!question) {
    alert("설문제목 입력!!");
    return;
  }
  let answerInputs = document.querySelectorAll("input[name=answer]");
  let isValid = true;
  answerInputs.forEach(function (answerInput) {
    if (!answerInput.value) {
      alert("답변항목 입력!!!");
      isValid = false;
      return false; //break;
      // return true; // continue;
    }
  });
  if (!isValid) return;
  let answers = [];
  answerInputs.forEach(function (answerInput) {
    answers.push(answerInput.value);
  });

  // let poll = {
  //   sdate: sdate,
  //   edate: edate,
  //   question: question,
  //   answers: answers
  // }
  // property shorthand(단축 속성명)
  let poll = {
    sdate,
    edate,
    question,
    answers,
  };

  localStorage.setItem("poll", JSON.stringify(poll));
  alert("설문조사 생성!!!");
  location.reload();
  // document.querySelector("#pollModal").modal("hide");
});
