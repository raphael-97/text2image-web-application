"use client";
import {
  Button,
  Popover,
  PopoverContent,
  PopoverTrigger,
  Textarea,
} from "@nextui-org/react";
import Image from "next/image";
import React, { useState } from "react";
import { CiImageOn } from "react-icons/ci";
import { notFound, useSearchParams } from "next/navigation";
import { GrGallery } from "react-icons/gr";
import { IoDownloadOutline } from "react-icons/io5";
import { ServerResponse } from "@/dto/errorResponse";

export default function ModelView({
  params: { id },
}: {
  params: { id: string };
}) {
  const searchParams = useSearchParams();
  const modelName = searchParams.get("name");
  if (!modelName) notFound();

  const [value, setValue] = useState<string>("");
  const [imageUrl, setImageUrl] = useState<string>("");
  const [imageBlob, setImageBlob] = useState<Blob | null>(null);
  const [loading, setIsLoading] = useState<boolean>(false);
  const [imageGenerationErrorMsg, setImageGenerationErrorMsg] =
    useState<string>("");

  const [disableUpload, setDisableUpload] = useState<boolean>(false);

  const [popOverColor, setPopOverColor] = useState<"success" | "danger">(
    "success"
  );
  const [popOverMessage, setPopOverMessage] = useState<string>("");

  const generateImage = async () => {
    setIsLoading(true);
    setImageGenerationErrorMsg("");
    try {
      const resData = await fetch(`/api/models/${id}`, {
        method: "POST",
        body: JSON.stringify(value),
      });

      if (resData.status === 201) {
        await resData.blob().then((blob) => {
          setImageUrl(URL.createObjectURL(blob));
          setImageBlob(blob);
        });
      }

      if (resData.status === 200) {
        const errorResponse: ServerResponse = await resData.json();
        setImageGenerationErrorMsg(errorResponse.message);
        setImageUrl("");
      }

      if (!resData.ok) {
        setImageGenerationErrorMsg("Server is not reachable, try again!");
      }
    } catch (error) {
      setImageGenerationErrorMsg("Server is not reachable, try again!");
    }

    setIsLoading(false);
  };

  const uploadToGallery = async () => {
    if (imageBlob) {
      setDisableUpload(true);

      const imageFile = new File([imageBlob], "image.jpg");

      const sendFormData = new FormData();

      sendFormData.append("file", imageFile, imageFile.name);

      try {
        const res = await fetch(`/api/images`, {
          method: "POST",
          body: sendFormData,
        });

        if (!res.ok) {
          setPopOverMessage("Server is not reachable, try again!");
          setPopOverColor("danger");
          return;
        }

        const serverResponse: ServerResponse = await res.json();

        serverResponse.success
          ? setPopOverColor("success")
          : setPopOverColor("danger");
        setPopOverMessage(serverResponse.message);
      } catch (error) {
        setPopOverMessage("Server is not reachable, try again!");
      }
    }
  };

  return (
    <div className="flex flex-col text-center mt-10 items-center">
      <h1 className=" text-3xl mb-10">{modelName}</h1>
      <div>
        {imageUrl ? (
          <div className="relative w-[512px] h-[512px]">
            <div className="absolute flex flex-row bottom-0 right-0 mb-4 mr-3 items-center">
              <a href={imageUrl} download={"image.jpg"}>
                <Button
                  disableRipple
                  className=" bg-transparent mb-1"
                  isIconOnly
                >
                  <IoDownloadOutline size={38}></IoDownloadOutline>
                </Button>
              </a>
              <Popover
                placement="right"
                showArrow
                color={popOverColor}
                onClose={() => setDisableUpload(false)}
              >
                <PopoverTrigger>
                  <Button
                    onClick={() => uploadToGallery()}
                    disableRipple
                    className=" bg-transparent"
                    isIconOnly
                    isDisabled={disableUpload}
                  >
                    <GrGallery size={30} />
                  </Button>
                </PopoverTrigger>
                <PopoverContent>
                  <div className="px-1 py-2">
                    <div className="text-small font-bold">{popOverMessage}</div>
                  </div>
                </PopoverContent>
              </Popover>
            </div>
            <div>
              <Image
                height={512}
                width={512}
                alt="generatedImage"
                src={imageUrl}
              ></Image>
            </div>
          </div>
        ) : (
          <div className="flex justify-center items-center bg-zinc-200 dark:bg-zinc-800 w-[512px] h-[512px]">
            <CiImageOn size={150} />
          </div>
        )}
      </div>
      {imageGenerationErrorMsg ? (
        <div className="flex justify-center align-middle mt-5">
          <p className=" text-danger">{imageGenerationErrorMsg}</p>
        </div>
      ) : (
        <></>
      )}

      <div className="mt-5 flex items-center">
        <Textarea
          variant="bordered"
          placeholder="Enter model input"
          value={value}
          onValueChange={setValue}
          className=" w-80"
        />
        <div className="ml-5 flex flex-col">
          <Button
            isLoading={loading}
            onClick={() => generateImage()}
            color="danger"
          >
            Compute
          </Button>
        </div>
      </div>
    </div>
  );
}
