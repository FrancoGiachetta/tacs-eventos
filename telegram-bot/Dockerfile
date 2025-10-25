FROM rust:1.89-alpine AS builder

WORKDIR /telegram-bot

RUN apk add --no-cache musl-dev openssl-dev openssl-libs-static

COPY ./Cargo.toml ./Cargo.lock ./
COPY ./src ./src

RUN cargo build --release

FROM alpine:3.19


WORKDIR /app

RUN apk add --no-cache ca-certificates libgcc && rm -rf /var/cache/apk/*

COPY --from=builder /telegram-bot/target/release/telegram-bot .

EXPOSE 42068
CMD ["./telegram-bot"]
