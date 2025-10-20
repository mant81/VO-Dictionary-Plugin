# VO Dictionary Plugin

**Project ID:** `com.sschoi.vodict.plugin`  
**Author:** SS Choi  
**Eclipse Version:** 2025-09  
**Java Version:** 21 (JavaSE-21 Execution Environment)

---

## 1. 개요

`VO Dictionary Plugin`은 Eclipse IDE에서 Java 프로젝트를 대상으로 하는 **맞춤 빌더와 Quick Fix**를 제공합니다.

- **Builder:** 프로젝트 내 Java 파일을 검사하고, 문제 마커 생성  
- **Nature:** 프로젝트에 플러그인 빌더 적용  
- **Marker:** 오류/경고 표시용 마커  
- **Quick Fix:** 마커 또는 코드 문제에 대한 자동 수정 기능 제공  

---

## 2. 설치 방법

1. **Update Site 설치 (권장)**  
   1. Eclipse 메뉴: `Help → Install New Software...`  
   2. `Add → Local...` 선택 후, `com.sschoi.vodict.site` 경로 지정  
   3. VO Dictionary Plugin 선택 후 설치  

2. **Feature 직접 설치**  
   1. Feature 프로젝트를 Eclipse에 Import  
   2. Feature → Plug-in 추가 확인 후 “Build All”  

---

## 3. 프로젝트 구조

com.sschoi.vodict.plugin/
├─ META-INF/
│ └─ MANIFEST.MF
├─ build.properties
├─ plugin.xml
└─ src/
└─ com/sschoi/vodict/plugin/
├─ builder/
│ ├─ VONature.java
│ └─ VOBuilder.java
└─ quickfix/
└─ QuickFixProcessor.java



---

## 4. 사용 방법

1. **프로젝트에 VO Nature 추가**
   - 프로젝트 우클릭 → `Configure → Add VO Nature`  
   - 빌더가 프로젝트에 등록되고, 파일 변경 시 자동으로 검사 수행

2. **마커 확인**
   - 문제 발생 시 Eclipse Problem View에 경고/오류 표시

3. **Quick Fix 사용**
   - Java 파일에서 마커 클릭 → `Ctrl+1`  
   - “VO Dictionary Quick Fix 적용” 선택 → 자동 수정

---

## 5. 요구 사항

- Eclipse 2025-09 이상
- JavaSE-21
- PDE Plug-in Development 환경 설치
- org.eclipse.core.runtime, org.eclipse.core.resources, org.eclipse.ui, org.eclipse.jdt.core, org.eclipse.jdt.ui 포함

---

## 6. 개발 참고

- **Bundle-SymbolicName:** `com.sschoi.vodict.plugin`  
- **Automatic-Module-Name:** `com.sschoi.vodict.plugin` (Java 모듈 시스템 호환)  
- **QuickFixProcessor**: Java 문제 마커 또는 VO 마커 기반 Quick Fix 제공  
- **VOBuilder**: 프로젝트 내 `.java` 파일 검사를 수행하며 마커 생성

---

## 7. 빌드 및 배포

1. Feature 프로젝트에서 Plug-in 추가  
2. Update Site 프로젝트에서 Feature 추가  
3. `Build All` → `/site` 폴더 생성  
4. Update Site 배포용으로 압축(zip) 후 Eclipse Install

---

## 8. 라이선스

MIT License (또는 회사 내부 정책에 맞게 수정)


