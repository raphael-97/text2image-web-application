"use client";
import { ModelResponse } from "@/dto/modelResponse";
import { Card, CardBody, CardFooter } from "@nextui-org/react";
import Image from "next/image";
import { useRouter } from "next/navigation";
import React from "react";

export const ModelCardComponent = (props: { model: ModelResponse }) => {
  const router = useRouter();
  const model = props.model;

  return (
    <Card
      shadow="sm"
      key={model.name}
      isPressable
      disableRipple
      onPress={() => router.push(`/explore/${model.id}?name=${model.name}`)}
      className="duration-1000 hover:scale-105 hover:bg-primary shadow-xl"
    >
      <CardBody className="overflow-hidden p-0 ">
        <Image
          priority
          src={`/api/models/${model.thumbnailImageId}`}
          alt={model.name}
          width={1000}
          height={1000}
          className="w-full h-[250px] object-cover duration-1000 hover:scale-125"
        ></Image>
      </CardBody>
      <CardFooter className="text-small justify-center">
        {model.name}
      </CardFooter>
    </Card>
  );
};
