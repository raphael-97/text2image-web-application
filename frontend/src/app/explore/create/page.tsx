import CreateModelForm from "@/components/CreateModelForm";
import { Card, CardBody } from "@nextui-org/react";
import React from "react";
export default function Create() {
  return (
    <div className="flex flex-col text-center mt-14 items-center">
      <p className="text-4xl">Create model</p>

      <Card className="max-w-full w-[340px] h-[350px] mt-8">
        <CardBody>
          <CreateModelForm />
        </CardBody>
      </Card>
    </div>
  );
}
