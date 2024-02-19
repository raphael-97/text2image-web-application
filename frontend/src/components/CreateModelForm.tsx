"use client";
import React, { useEffect } from "react";
import { createModelAction } from "@/app/lib/modelActions";
import { Button, Input } from "@nextui-org/react";
import { useRef, useState } from "react";
import { MdOutlineFileUpload } from "react-icons/md";
import { useFormState } from "react-dom";
import { useRouter } from "next/navigation";

const messageInit = "";

export default function CreateModelForm() {
  const [pathValue, setPathValue] = useState("");

  const [serverMessage, action] = useFormState(createModelAction, messageInit);

  const inputFile = useRef<HTMLInputElement | null>(null);

  const router = useRouter();

  useEffect(() => {
    if (serverMessage === "success") router.push("/explore");
  }, [serverMessage]);

  const onButtonClick = () => {
    if (inputFile.current !== null) {
      inputFile.current.click();
    }
  };
  return (
    <form action={action} className="px-5">
      <Input
        className="pt-5"
        label="Model name"
        name="name"
        isRequired
        placeholder="Enter model name"
        type="text"
      />
      <Input
        className="pt-5"
        label="Inference url"
        name="inferenceUrl"
        isRequired
        placeholder="Enter the api inference url"
        type="text"
      />

      <input
        type="file"
        id="file"
        ref={inputFile}
        style={{ display: "none" }}
        name="file"
        onChange={(e) => setPathValue(e.target.value)}
      ></input>

      <Input
        isReadOnly
        isRequired
        type="text"
        label="Filepath"
        name="Filepath"
        value={pathValue}
        className="pt-5"
        onClick={onButtonClick}
        endContent={<MdOutlineFileUpload size={30} />}
      />
      <div className="pt-5">
        <Button type="submit" fullWidth color="primary">
          Create Model
        </Button>
      </div>
    </form>
  );
}
