# java-innodb-jcole-translation

이 프로젝트는 Jeremy Cole의 블로그 [InnoDB Blog Posts](https://blog.jcole.us/innodb/)를 기반으로 MySQL의 InnoDB 스토리지 엔진의 내부 구조와 동작 원리를 Java로 재해석한 학습용 자료입니다.

원문은 Ruby 기반이지만, 본 레포지토리는 **Java 개발자도 쉽게 이해할 수 있도록 예제 코드를 Java로 재구성**하고, **중요 개념과 내부 동작을 해설**하여 MySQL InnoDB의 구조를 깊이 있게 이해할 수 있도록 돕습니다.

---

## 📌 프로젝트 목적

- Jeremy Cole의 InnoDB 블로그 글을 번역 및 요약
- Ruby 예제를 Java 기반 코드로 재작성
- Java 개발자 친화적인 DB 내부 동작 학습 자료 제공
- InnoDB의 페이지 구조, Undo/Redo, 트랜잭션 처리 방식 등 핵심 메커니즘 설명

---

## 📚 목차 및 구조

- `/docs`
  - 각 블로그 글 번역 (Markdown 형식)
  - 핵심 개념 요약 및 정리
- `/src`
  - Java 기반의 예제 코드
  - InnoDB 동작을 Java 객체로 시뮬레이션하거나 설명
  - 일부 개념에 대한 단위 테스트
- `/reference`
  - 원문 링크 및 관련 문서 요약

---

## 🔗 원문 링크

- [Jeremy Cole’s InnoDB blog series](https://blog.jcole.us/innodb/)

> 본 프로젝트는 원문의 구조와 의도를 최대한 존중하며 학습 목적으로 재구성하였습니다. 모든 저작권은 원 저자에게 있습니다.

---

## ✅ 진행 방식

- 각 글 단위로 번역 및 요약
- 번역문 + Java 예제 + 해설 조합으로 학습 구조화
- 학습 목적이므로 코드나 설명에 개선 여지가 있으면 issue 또는 PR로 환영합니다

---

## 📌 기여 안내

- 오타, 해설 오류, 코드 개선 등 자유롭게 PR 주세요
- 필요한 경우 자유롭게 의견을 남겨주세요

---

## 📄 라이선스

이 프로젝트는 학습용으로 공개되며, 원문 블로그의 저작권은 Jeremy Cole에게 있습니다.  
코드 및 번역 결과물은 Creative Commons Attribution-NonCommercial-ShareAlike 라이선스를 따릅니다.
