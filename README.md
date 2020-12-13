# tater

## 使い方

#### ローカル環境

* 初回は以下のように local.properties のリンクを所定の場所に作成する

```bash
cd /etc
sudo mkdir tater & cd tater
sudo ln -s <app directory>/environment/tater-api/local.properties app.properties
```

* IntelliJなどでアプリを実行する (DB, wiremockはE2E環境を参照)

#### E2E環境 (k8s namespace=tater-e2e)

* 初回は以下のように Namespace と Secret を作成する

```bash
cd <app directory>/environment & kubectl apply -f namespace-e2e.yml
cd <app directory>/environment/tater-api
./create-secret.sh tater-e2e e2e.properties
```

* 必要に応じて Docker Image をビルドする

```bash
cd <app directory>/environment/tater-api & ./build-image.sh
cd <app directory>/environment/tater-db & ./build-image.sh
```

* `environment/prep-e2e-env.sh` でE2E用の環境を全て構築
* `environment/clean-e2e-env.sh` でE2E用の環境を全て削除

#### 本番環境 (k8s namespace=tater-prd)

* 初回は以下のように Namespace と Secret を作成する

```bash
cd <app directory>/environment & kubectl apply -f namespace-prd.yml
cd <app directory>/environment/tater-api
cp production-credentials.template.txt production-credentials.txt

# production-credentials.txtを編集する

# credentials を適用した production.properties を作成
# MacOSでは生成したファイルの末尾に空行が入れられるので注意 ( gsed を使うか、手動で空行を消すこと)
./replace-credentials.sh production.template.properties production-credentials.txt production.properties

# Secretを作成
./create-secret.sh tater-prd production.properties
```

* `environment/prep-prd-env.sh` で本番用の環境を全て構築
* `environment/clean-prd-env.sh` で本番用の環境を全て削除

* 必要に応じて以下のコマンドでデータを投入する

```bash
cd <app directory>/environment/tater-db/samples
psql -h localhost -p 19002 -U tater -f seed.sql
```