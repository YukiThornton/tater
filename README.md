# tater

## taterについて

* プログラミング/設計勉強用として開発している (本格運用の予定はない)
* 見るべき映画をオススメしてくれるアプリ
  * API: Kotlin
* 情報源として <a href="https://www.themoviedb.org/"><img src="tmdb-logo.svg" width="80" alt="The Movie DB Logo"></a> を利用している

## 使い方

#### API=ローカル環境 + それ以外=k8s環境

* 初回は以下のように local.properties のリンクを所定の場所に作成する

```bash
cd /etc
sudo mkdir tater & cd tater
sudo ln -s <app directory>/environment/tater-api/local.properties app.properties
```

* skaffoldでDB, Wiremockを起動する

```bash
skaffold dev -p no-api
```

* IntelliJなどでアプリを実行する (DB, wiremockはE2E環境を参照)

#### E2E環境 (k8s namespace=tater-e2e)

* 初回は以下のように Namespace を作成する

```bash
cd <app directory>/environment & kubectl apply -f namespace-e2e.yml
```

* skaffoldで起動する

```bash
skaffold dev
```

#### 本番環境 (k8s namespace=tater-prd)

* 初回は以下のように Namespace と Secret 用ファイルを作成する

```bash
cd <app directory>/environment & kubectl apply -f namespace-prd.yml
cd <app directory>/environment/tater-api
cp production-credentials.template.txt production-credentials.txt

# production-credentials.txtを編集する

# credentials を適用した production.properties を作成
# MacOSでは生成したファイルの末尾に空行が入れられるので注意 ( gsed を使うか、手動で空行を消すこと)
cd <app directory>/environment/bin && ./replace-credentials.sh ../tater-api/helm/secrets/production.template.properties ../tater-api/production-credentials.txt ../tater-api/helm/secrets/production.properties
```

* skaffoldで起動/停止する

```bash
skaffold run -f skaffold.prd.yaml --port-forward=user --tail
skaffold delete -f skaffold.prd.yaml
```

* 必要に応じて以下のコマンドでデータを投入する

```bash
cd <app directory>/environment/tater-db/samples
psql -h localhost -p 19002 -U tater -f seed.sql
```