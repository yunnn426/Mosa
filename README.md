# Mosa

> My Optimal Styling Assistant
>
> 퍼스널 컬러 기반 스타일링 추천과 컬러 진단 애플리케이션

## 시스템 구성도
![시스템 구성도 (1)](https://github.com/yunnn426/Mosa/assets/95147113/e77f49b4-4f20-4ea1-b9cb-783249767caa)


## 사용자의 사진에서 컬러를 추출하는 방법
- 해당 기능은 파이썬을 통해 구현하였습니다. 컬러 추출 방법에 대한 설명은 아래 링크에 서술하였습니다.
- <https://github.com/yunnn426/FaceColor>

## 얼굴형 분석 모델
- 하트형(heart), 직사각형(oblong), 타원형(oval), 둥근형(round), 각진형(square)
- [학습에 사용한 데이터셋](https://www.kaggle.com/datasets/niten19/face-shape-dataset/data)
- tflite 모델을 앱 내에 저장
- 사용자의 사진에 대한 얼굴형을 처리하고 결과값을 반환

## 애플리케이션 동작 과정
1. 사용자는 자신의 얼굴 사진을 업로드
2. 컬러 진단기를 통해 사용자의 퍼스널 컬러 진단
3. 퍼스널 컬러에 따른 여러 스타일링 아이템을 추천

   
![Group 38 (3)](https://github.com/yunnn426/Mosa/assets/95147113/d81bb9f1-e64a-4291-b1b0-f4162c842e55)



## 기타 기능들
- 회원 관리: 로그인, 회원가입, 비밀번호 찾기, 회원정보 수정
- 앱 사용을 위한 튜토리얼 제공
- 지난 진단 기록 다시 보기
